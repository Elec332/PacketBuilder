package nl.elec332.lib.packetbuilder.impl.fields;

import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.IPacketFieldManager;
import nl.elec332.lib.packetbuilder.api.util.IntReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.fields.NumberField;
import nl.elec332.lib.packetbuilder.fields.SimpleField;
import nl.elec332.lib.packetbuilder.fields.UnsignedNumberField;
import nl.elec332.lib.packetbuilder.fields.generic.BitsField;
import nl.elec332.lib.packetbuilder.fields.generic.MappedField;
import nl.elec332.lib.packetbuilder.fields.generic.SimpleConditionalField;
import nl.elec332.lib.packetbuilder.fields.generic.VariableLengthField;
import nl.elec332.lib.packetbuilder.impl.fields.numbers.*;
import nl.elec332.lib.packetbuilder.util.LazyValue;
import nl.elec332.lib.packetbuilder.util.NumberHelper;
import nl.elec332.lib.packetbuilder.util.StringHelper;
import nl.elec332.lib.packetbuilder.util.reflection.ReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 2/28/2021
 */
public class FieldRegister {

    private static Supplier<AbstractField<Object>> getField(AbstractPacketObject packet, String field) {
        return getField(packet, field, null);
    }

    @SuppressWarnings("unchecked")
    private static Supplier<AbstractField<Object>> getField(AbstractPacketObject packet, String field, Consumer<AbstractField<Object>> modifier) {
        return new LazyValue<>(() -> {
            String[] fields = field.split("\\.");
            AbstractField<?> ret = packet.getAllFields().get(fields[0]);
            for (int i = 1; i < fields.length; i++) {
                ret = ((AbstractPacketObject) ret.get()).getAllFields().get(fields[i]);
            }
            return (AbstractField<Object>) ret;
        }, modifier);
    }

    @SuppressWarnings("unchecked")
    private static <T> Supplier<T> getFieldMethodObjectSupplier(AbstractPacketObject packet, String name) {
        Supplier<Object> supplier;
        Method m = ReflectionHelper.getMethod(packet.getClass(), name);
        if (m != null) {
            m.setAccessible(true);
            supplier = () -> {
                try {
                    return m.invoke(packet);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        } else {
            Supplier<AbstractField<Object>> field = getField(packet, name);
            supplier = () -> field.get().get();
        }
        return (Supplier<T>) supplier;
    }

    private static <T> T instantiate(Constructor<T> constructor, Object root, Object... params) throws Exception {
        if (constructor.getParameterCount() == params.length) {
            return constructor.newInstance(params);
        } else if (constructor.getParameterCount() == params.length + 1) {
            Object[] p = new Object[params.length + 1];
            System.arraycopy(params, 0, p, 1, params.length);
            p[0] = root;
            return constructor.newInstance(p);
        } else {
            throw new RuntimeException();
        }
    }

    public static void registerFields(IPacketFieldManager fieldManager) {
        fieldManager.registerFieldFactory(SimpleField.class, (annotation, packet, type, value) -> {
            try {
                return instantiate(ReflectionHelper.getConstructor(annotation.value(), ValueReference.class), packet, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        fieldManager.registerFieldFactory(BitsField.class, (annotation, packet, type, value) -> {
            try {
                return instantiate(ReflectionHelper.getConstructor(annotation.value(), ValueReference.class, int.class, int.class), packet, value, annotation.bits(), annotation.startBit());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        fieldManager.registerFieldFactory(VariableLengthField.class, (annotation, packet, type, value) -> {
            try {
                String length = annotation.length();
                IntReference supplier;
                if (StringHelper.isNullOrEmpty(length)) {
                    supplier = null;
                } else {
                    try {
                        final int len = Integer.parseInt(length);
                        supplier = IntReference.wrap(() -> len, null);
                    } catch (NumberFormatException expected) {
                        Method m = ReflectionHelper.getMethod(packet.getClass(), length);
                        if (m != null) {
                            m.setAccessible(true);
                            supplier = IntReference.wrap(() -> {
                                try {
                                    return NumberHelper.toInt(m.invoke(packet));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }, null);
                        } else {
                            Supplier<AbstractField<Object>> field = getField(packet, length, f -> f.setDelayed(true));
                            supplier = IntReference.wrap(() -> NumberHelper.toInt(field.get().get()), i -> field.get().set(i));
                        }
                    }
                }
                return instantiate(ReflectionHelper.getConstructor(annotation.value(), ValueReference.class, IntReference.class), packet, value, supplier);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        fieldManager.registerFieldFactory(SimpleConditionalField.class, (annotation, packet, type, value) -> {
            try {
                BooleanSupplier predicate;
                String method = annotation.method();
                Supplier<Object> supplier = getFieldMethodObjectSupplier(packet, method);
                if (annotation.numberValue() != Long.MIN_VALUE) {
                    predicate = () -> NumberHelper.toLong(supplier.get()) == annotation.numberValue();
                } else {
                    predicate = () -> (boolean) supplier.get();
                }
                return new nl.elec332.lib.packetbuilder.impl.fields.optional.SimpleConditionalField<>((AbstractField<?>) value.get(), predicate);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        fieldManager.registerFieldFactory(NumberField.class, (annotation, packet, type, value) -> {
            Class<?> typ = getNumberClass(annotation.value(), type);
            if (typ == Byte.class) {
                return new ByteField(value.cast());
            }
            if (typ == Short.class) {
                return new ShortField(value.cast());
            }
            if (typ == Integer.class) {
                return new IntField(value.cast());
            }
            if (typ == Long.class) {
                return new LongField(value.cast());
            }
            if (typ == Float.class) {
                return new FloatField(value.cast());
            }
            if (typ == Double.class) {
                return new DoubleField(value.cast());
            }
            throw new UnsupportedOperationException();
        });
        fieldManager.registerFieldFactory(UnsignedNumberField.class, (annotation, packet, type, value) -> {
            if (annotation.value() != Number.class) {
                Class<?> typ = NumberHelper.unWrap(annotation.value());
                if (typ == Byte.class) {
                    return new UnsignedByteField(value.cast());
                }
                if (typ == Short.class) {
                    return new UnsignedShortField(value.cast());
                }
                if (typ == Integer.class) {
                    return new UnsignedIntField(value.cast());
                }
                throw new UnsupportedOperationException("Unsigned not supported for annotation type: " + typ);
            }
            Class<?> typ = NumberHelper.unWrap(type);
            if (typ == Short.class) {
                return new UnsignedByteField(value.cast());
            }
            if (typ == Integer.class) {
                return new UnsignedShortField(value.cast());
            }
            if (typ == Long.class) {
                return new UnsignedIntField(value.cast());
            }
            throw new UnsupportedOperationException("Unsigned not supported for field type : " + typ);
        });
        fieldManager.registerFieldFactory(MappedField.class, (annotation, packet, type, value) -> {
            try {
                Field f = ReflectionHelper.getField(packet.getClass(), annotation.field());
                int i = NumberHelper.toInt(f.get(packet));
                int idx = 0;
                int[] vals = annotation.values();
                while (vals[idx] != i) {
                    idx++;
                    if (idx >= vals.length) {
                        throw new RuntimeException("Value not defined: " + i);
                    }
                }
                return instantiate(ReflectionHelper.getConstructor(annotation.valueMap()[idx], ValueReference.class), packet, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static Class<? extends Number> getNumberClass(Class<?> one, Class<?> two) {
        if (one == Number.class) {
            one = two;
        }
        Class<? extends Number> ret = NumberHelper.unWrap(one);
        if (ret == Number.class || !Number.class.isAssignableFrom(ret)) {
            throw new IllegalArgumentException("Invalid number class: " + ret);
        }
        return ret;
    }

}
