package nl.elec332.lib.packetbuilder.internal;

import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.IPacketFieldManager;
import nl.elec332.lib.packetbuilder.api.field.IFieldFactory;
import nl.elec332.lib.packetbuilder.api.field.PacketField;
import nl.elec332.lib.packetbuilder.api.field.PacketFieldWrapper;
import nl.elec332.lib.packetbuilder.api.field.RegisteredField;
import nl.elec332.lib.packetbuilder.impl.fields.FieldRegister;
import nl.elec332.lib.packetbuilder.util.ValueReference;
import nl.elec332.lib.packetbuilder.util.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 2/26/2021
 */
public enum PacketFieldManager implements IPacketFieldManager {

    INSTANCE;

    private final Map<Class<? extends AbstractPacketObject>, List<Map.Entry<String, Function<AbstractPacketObject, AbstractField<?>>>>> CACHED_FIELDS = new HashMap<>();
    private final Map<Class<? extends Annotation>, IFieldFactory<Annotation, Object>> FIELD_FACTORIES = new HashMap<>();
    private final Set<String> ACCESS = new HashSet<>();

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> void registerFieldFactory(Class<A> annotation, IFieldFactory<A, ?> factory) {
        if (FIELD_FACTORIES.containsKey(annotation)) {
            throw new IllegalArgumentException("Annotation already registered: " + annotation);
        }
        FIELD_FACTORIES.put(annotation, (IFieldFactory<Annotation, Object>) factory);
    }

    public Map<String, AbstractField<?>> getFields(AbstractPacketObject packet, Map<Field, Function<Object, AbstractField<?>>> factories) {
        return CACHED_FIELDS.computeIfAbsent(packet.getClass(), p -> ReflectionHelper.getFields(p, RegisteredField.class, AbstractPacketObject.class).stream()
                .map(f -> {
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
                    return new AbstractMap.SimpleEntry<>(name, getFactoryFor(f, factories));
                })
                .collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(Map.Entry::getKey, pair -> {
                    try {
                        return pair.getValue().apply(packet);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to initialize field: " + pair.getKey(), e);
                    }
                }, (o1, o2) -> {
                    throw new RuntimeException("Double names in packet type " + packet.getClass() + "!");
                }, LinkedHashMap::new));
    }

    @SuppressWarnings("unchecked")
    private Function<AbstractPacketObject, AbstractField<?>> getFactoryFor(Field f, Map<Field, Function<Object, AbstractField<?>>> factories) {
        Function<AbstractPacketObject, AbstractField<?>> factory;
        Annotation fAnn = null;
        Annotation wAnn = null;
        for (Annotation ann : f.getDeclaredAnnotations()) {
            if (FIELD_FACTORIES.containsKey(ann.annotationType())) {
                if (ann.annotationType().isAnnotationPresent(PacketFieldWrapper.class)) {
                    if (wAnn == null) {
                        wAnn = ann;
                    } else {
                        throw new IllegalArgumentException("Field " + f.getName() + " in " + f.getDeclaringClass() + " has multiple wrapper types!");
                    }
                } else if (ann.annotationType().isAnnotationPresent(PacketField.class)) {
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
                factory = o -> fif.instantiate(finalFAnn, o, (Class<Object>) f.getType(), ReflectionHelper.wrapField(f, o));
            } else {
                if (!AbstractField.class.isAssignableFrom(f.getType())) {
                    throw new UnsupportedOperationException("Field not instanceof AbstractField: " + f);
                }
                factory = o -> {
                    try {
                        return (AbstractField<?>) f.get(o);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
            }

            if (wAnn != null) {
                IFieldFactory<Annotation, Object> fif = Objects.requireNonNull(FIELD_FACTORIES.get(wAnn.annotationType()));
                Annotation finalWAnn = wAnn;
                final Function<AbstractPacketObject, AbstractField<?>> mainFactory = factory;
                factory = o -> {
                    AbstractField<?> field = mainFactory.apply(o);
                    Class<?> clazz = field.getClass();
                    return fif.instantiate(finalWAnn, o, (Class<Object>) clazz, new ValueReference<>(field));
                };
            }
        }
        return factory;
    }

    static {
        FieldRegister.registerFields(INSTANCE);
    }

}
