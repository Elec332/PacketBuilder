package nl.elec332.lib.packetbuilder.internal;

import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.IFieldFactory;
import nl.elec332.lib.packetbuilder.api.IPacketFieldManager;
import nl.elec332.lib.packetbuilder.api.field.*;
import nl.elec332.lib.packetbuilder.api.util.FieldWrapper;
import nl.elec332.lib.packetbuilder.api.util.RegisteredField;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 2/26/2021
 */
public enum PacketFieldManager implements IPacketFieldManager {

    INSTANCE;

    private final Map<Class<? extends AbstractPacketObject>, List<Map.Entry<String, Function<AbstractPacketObject, AbstractField>>>> CACHED_FIELDS = new HashMap<>();
    private final Map<Class<? extends Annotation>, IFieldFactory<Annotation, Object>> FIELD_FACTORIES = new HashMap<>();
    private final Set<String> ACCESS = new HashSet<>();

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> void registerFieldFactory(Class<A> annotation, IFieldFactory<A, ?> factory) {
        FIELD_FACTORIES.put(annotation, (IFieldFactory<Annotation, Object>) factory);
    }

    public Map<String, AbstractField> getFields(AbstractPacketObject packet, Map<Field, Function<Object, AbstractField>> factories) {
        return CACHED_FIELDS.computeIfAbsent(packet.getClass(), p -> {
            List<Map.Entry<String, Function<AbstractPacketObject, AbstractField>>> ret = new ArrayList<>();
            for (Field f : p.getDeclaredFields()) {
                if (f.isAnnotationPresent(RegisteredField.class)) {
                    String packName = p.getPackageName();
                    if (!ACCESS.contains(packName)) {
                        getClass().getModule().addReads(p.getModule());
                        ACCESS.add(packName);
                    }
                    f.setAccessible(true);
                    RegisteredField data = f.getAnnotation(RegisteredField.class);
                    String name = data.value();
                    if (name.isEmpty()) {
                        name = f.getName();
                    }
                    ret.add(new AbstractMap.SimpleEntry<>(name, getFactoryFor(f, factories)));
                }
            }
            return ret;
        }).stream()
                .collect(Collectors.toMap(Map.Entry::getKey, pair -> {
                    try {
                        return pair.getValue().apply(packet);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to initialize field: " + pair.getKey(), e);
                    }
                }, (o1, o2) -> {
                    throw new RuntimeException("Double names in packet type " + packet.getClass() + "!");
                }, LinkedHashMap::new));
//                .map(pair -> {
//                    try {
//                        //AbstractField field = (AbstractField) pair.getValue().getType().getConstructor().newInstance();
//                        //pair.getValue().set(packet, field);
//                        Map.Entry<Field, Function<Object, AbstractField>> e2 = pair.getValue();
//                        return new AbstractMap.SimpleEntry<>(pair.getKey(), e2.getValue().apply(e2.getKey().get(packet)));
//                    } catch (Exception e) {
//                        throw new RuntimeException("Failed to initialize field: " + pair.getValue(), e);
//                    }
//                })
//                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Function<AbstractPacketObject, AbstractField> getFactoryFor(Field f, Map<Field, Function<Object, AbstractField>> factories) {
        Function<AbstractPacketObject, AbstractField> factory;
        Annotation fAnn = null;
        Annotation wAnn = null;
        for (Annotation ann : f.getDeclaredAnnotations()) {
            if (FIELD_FACTORIES.containsKey(ann.annotationType())) {
                if (ann.annotationType().isAnnotationPresent(FieldWrapper.class)) {
                    if (wAnn == null) {
                        wAnn = ann;
                    } else {
                        throw new IllegalArgumentException("Field " + f.getName() + " in " + f.getDeclaringClass() + " has multiple wrapper types!");
                    }
                } else {
                    if (fAnn == null) {
                        fAnn = ann;
                    } else {
                        throw new IllegalArgumentException("Field " + f.getName() + " in " + f.getDeclaringClass() + " has multiple field types!");
                    }
                }
            }
        }
        if (factories != null && factories.containsKey(f)) {
            if (fAnn != null) {
                throw new RuntimeException();
            }
            factory = pkt -> {
                try {
                    return Objects.requireNonNull(factories.get(f).apply(f.get(pkt)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        } else {
            if (fAnn != null) {
                IFieldFactory<Annotation, Object> fif = Objects.requireNonNull(FIELD_FACTORIES.get(fAnn.annotationType()));
                Annotation finalFAnn = fAnn;
                factory = o -> {
                    //return data.fieldType().getConstructor(f.getType()).newInstance(v);
                    Object obj;
                    try {
                        obj = f.get(o);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                    return new WrappedReflectionField(fif.instantiate(finalFAnn, o, (Class<Object>) f.getType(), obj), o, f);
                };
            } else {
                if (!AbstractField.class.isAssignableFrom(f.getType())) {
                    throw new UnsupportedOperationException("Field not instanceof AbstractField: " + f);
                }
                factory = o -> {
                    try {
                        return (AbstractField) f.get(o);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
            }

            if (wAnn != null) {
                IFieldFactory<Annotation, Object> fif = Objects.requireNonNull(FIELD_FACTORIES.get(wAnn.annotationType()));
                Annotation finalWAnn = wAnn;
                final Function<AbstractPacketObject, AbstractField> mainFactory = factory;
                factory = o -> {
                    AbstractField field = mainFactory.apply(o);
                    Class<?> clazz = field.getClass();
                    return fif.instantiate(finalWAnn, o, (Class<Object>) clazz, field);
                };
            }


//                        if (data.fieldType() == AbstractField.class) {
//                        } else {
//                            factory = o -> {
//                                //return data.fieldType().getConstructor(f.getType()).newInstance(v);
//                                return new WrappedField(data.fieldType(), o, f);
//                            };
//                        }
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?> culprit, Class<?>... otherParams) {
        Constructor<T> ret;
        try {
            ret = clazz.getConstructor(add(culprit, otherParams));
            ret.setAccessible(true);
            return ret;
        } catch (NoSuchMethodException e) {
            for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
                if (ctor.getParameterCount() == otherParams.length + 1 && ctor.getParameterTypes()[0].isAssignableFrom(culprit)) {
                    ret = (Constructor<T>) ctor;
                    ret.setAccessible(true);
                    return ret;
                }
            }
            throw new RuntimeException(e);
        }
    }

    private static Class<?>[] add(Class<?> culprit, Class<?>... otherParams) {
        Class<?>[] ret = new Class[otherParams.length + 1];
        ret[0] = culprit;
        System.arraycopy(otherParams, 0, ret, 1, otherParams.length);
        return ret;
    }

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        INSTANCE.registerFieldFactory(SimpleField.class, (annotation, packet, type, value) -> {
            try {
                return getConstructor(annotation.value(), type).newInstance(value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        INSTANCE.registerFieldFactory(BitsField.class, (annotation, packet, type, value) -> {
            try {
                return getConstructor(annotation.value(), type, int.class, int.class).newInstance(value, annotation.bits(), annotation.startBit());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        INSTANCE.registerFieldFactory(VariableLengthField.class, (annotation, packet, type, value) -> {
            try {
                return getConstructor(annotation.value(), type, int.class).newInstance(value, (IntSupplier) () -> ((Number) packet.getAllFields().get(annotation.lengthField()).getValue()).intValue());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        INSTANCE.registerFieldFactory(SimpleConditionalField.class, (annotation, packet, type, value) -> {
            try {
                BooleanSupplier predicate;
                if (annotation.numberValue() == Long.MIN_VALUE) {
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
                        check = lookup.unreflect(packet.getClass().getDeclaredMethod(annotation.method()));
                    } catch (NoSuchMethodException e) {
                        check = lookup.unreflectGetter(packet.getClass().getDeclaredField(annotation.method()));
                    }
                    MethodHandle finalCheck = check;
                    predicate = () -> {
                        try {
                            return ((Number) finalCheck.invoke(packet)).longValue() == annotation.numberValue();
                        } catch (Throwable t) {
                            throw new RuntimeException(t);
                        }
                    };
                }
                return new nl.elec332.lib.packetbuilder.impl.fields.optional.SimpleConditionalField<>((AbstractField) value, predicate);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
