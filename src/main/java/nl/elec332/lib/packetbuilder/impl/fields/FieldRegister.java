package nl.elec332.lib.packetbuilder.impl.fields;

import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.api.IPacketFieldManager;
import nl.elec332.lib.packetbuilder.api.util.IValueReference;
import nl.elec332.lib.packetbuilder.fields.NumberField;
import nl.elec332.lib.packetbuilder.fields.SimpleField;
import nl.elec332.lib.packetbuilder.fields.UnsignedNumberField;
import nl.elec332.lib.packetbuilder.fields.generic.BitsField;
import nl.elec332.lib.packetbuilder.fields.generic.SimpleConditionalField;
import nl.elec332.lib.packetbuilder.fields.generic.VariableLengthField;
import nl.elec332.lib.packetbuilder.impl.fields.numbers.*;
import nl.elec332.lib.packetbuilder.util.NumberHelper;
import nl.elec332.lib.packetbuilder.util.StringHelper;
import nl.elec332.lib.packetbuilder.util.reflection.ReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

/**
 * Created by Elec332 on 2/28/2021
 */
public class FieldRegister {

    public static void registerFields(IPacketFieldManager fieldManager) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        fieldManager.registerFieldFactory(SimpleField.class, (annotation, packet, type, value) -> {
            try {
                Constructor<? extends AbstractField<?>> constructor = ReflectionHelper.getConstructor(annotation.value(), IValueReference.class);
                if (ReflectionHelper.isNonStaticInternalClass(annotation.value())) {
                    return constructor.newInstance(packet, value);
                } else {
                    return constructor.newInstance(value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        fieldManager.registerFieldFactory(BitsField.class, (annotation, packet, type, value) -> {
            try {
                return ReflectionHelper.getConstructor(annotation.value(), IValueReference.class, int.class, int.class).newInstance(value, annotation.bits(), annotation.startBit());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        fieldManager.registerFieldFactory(VariableLengthField.class, (annotation, packet, type, value) -> {
            try {
                String length = annotation.length();
                IntSupplier supplier;
                if (StringHelper.isNullOrEmpty(length)) {
                    supplier = null;
                } else {
                    try {
                        final int len = Integer.parseInt(length);
                        supplier = () -> len;
                    } catch (NumberFormatException expected) {
                        try {
                            Method m = packet.getClass().getDeclaredMethod(length);
                            supplier = () -> {
                                try {
                                    return NumberHelper.toInt(m.invoke(packet));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            };
                        } catch (NoSuchMethodException expectedAgain) {
                            supplier = () -> NumberHelper.toInt(packet.getAllFields().get(length).get());
                        }
                    }
                }
                return ReflectionHelper.getConstructor(annotation.value(), IValueReference.class, IntSupplier.class).newInstance(value, supplier);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        fieldManager.registerFieldFactory(SimpleConditionalField.class, (annotation, packet, type, value) -> {
            try {
                BooleanSupplier predicate;
                if (annotation.numberValue() != Long.MIN_VALUE) {
                    MethodHandle check = lookup.unreflect(packet.getClass().getDeclaredMethod(annotation.method()));
                    predicate = () -> {
                        try {
                            return (boolean) check.invoke(packet);
                        } catch (Throwable t) {
                            throw new RuntimeException(t);
                        }
                    };
                } else {
                    MethodHandle check;
                    try {
                        Method m = ReflectionHelper.getMethod(packet.getClass(), annotation.method());
                        m.setAccessible(true);
                        check = lookup.unreflect(m);
                    } catch (Exception e) {
                        check = lookup.unreflectGetter(packet.getClass().getDeclaredField(annotation.method()));
                    }
                    MethodHandle finalCheck = check;
                    predicate = () -> {
                        try {
                            return ((Boolean) finalCheck.invoke(packet));
                        } catch (Throwable t) {
                            throw new RuntimeException(t);
                        }
                    };
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
