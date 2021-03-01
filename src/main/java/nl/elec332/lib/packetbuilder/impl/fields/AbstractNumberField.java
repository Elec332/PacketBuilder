package nl.elec332.lib.packetbuilder.impl.fields;

import nl.elec332.lib.packetbuilder.api.util.TypedValueReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.util.NumberHelper;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Created by Elec332 on 2/28/2021
 */
public abstract class AbstractNumberField<T extends Number> extends AbstractSimpleField<T> {

    public AbstractNumberField(ValueReference<T> reference) {
        super(reference);
        type = Objects.requireNonNull(getType());
    }


    public AbstractNumberField(ValueReference<T> reference, Class<T> type) {
        super(reference);
        this.type = type;
    }

    private final Class<T> type;

    @SuppressWarnings("unchecked")
    private Class<T> getType() {
        Class<?> clazz = getClass();
        Class<T> type = null;
        searchLoop:
        while (clazz != AbstractNumberField.class && type == null) {
            boolean annotated = clazz.getAnnotatedSuperclass() instanceof AnnotatedParameterizedType;
            if (!annotated) {
                clazz = clazz.getSuperclass();
                continue;
            }
            AnnotatedType[] types = ((AnnotatedParameterizedType) clazz.getAnnotatedSuperclass()).getAnnotatedActualTypeArguments();
            if (types.length == 0) {
                clazz = clazz.getSuperclass();
                continue;
            }
            for (AnnotatedType t : types) {
                Type typ = t.getType();
                if (typ.getClass() == Class.class && Number.class.isAssignableFrom((Class<?>) typ)) {
                    type = (Class<T>) typ;
                    continue searchLoop;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return type;
    }

    @Override
    public T get() {
        return NumberHelper.cast(super.get(), type);
    }

    @Override
    public void accept(T number) {
        super.accept(NumberHelper.cast(number, ((TypedValueReference<T>) valueReference).getType()));
    }

    @Override
    protected boolean valuesEqual(Number me, Number other) {
        return NumberHelper.equals(me, other);
    }

}
