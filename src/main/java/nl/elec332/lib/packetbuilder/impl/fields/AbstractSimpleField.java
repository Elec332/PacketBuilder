package nl.elec332.lib.packetbuilder.impl.fields;

import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.api.util.IValueReference;

import java.util.Objects;

/**
 * Created by Elec332 on 2/26/2021
 */
public abstract class AbstractSimpleField<T> extends AbstractField<T> {
//
//    @SuppressWarnings("unchecked")
//    public AbstractSimpleField(T value) {
//        this((Class<T>) value.getClass(), new ValueReference<>(value));
//    }

    public AbstractSimpleField(/*Class<T> type, */IValueReference<T> reference) {
        //this.type = type;
        this.valueReference = reference;
    }

//    @SuppressWarnings("unchecked")
//    public AbstractSimpleField() {
//        Class<?> type;
//        IValueReference<T> valueRef;
//        try {
//            Field value = null;
//            Class<?> clazz = getClass();
//            while (clazz != Object.class && value == null) {
//                try {
//                    value = clazz.getDeclaredField("value");
//                } catch (Exception e) {
//                    clazz = clazz.getSuperclass();
//                }
//            }
//            if (value == null) {
//                throw new NoSuchFieldException("value");
//            }
//            valueRef = ReflectionHelper.wrapField(value, this);
//            type = value.getType();
//        } catch (Exception e) {
//            throw new RuntimeException("A field must have a value!", e);
//        }
//        this.type = (Class<T>) type;
//        this.valueReference = valueRef;
//    }

    protected final IValueReference<T> valueReference;

    @Override
    public T get() {
        return valueReference.get();
    }

    @Override
    public void accept(T t) {
        if (valueReference.hasSetter()) {
            valueReference.accept(t);
        } else {
            if (!valuesEqual(get(), t)) {
                throw new IllegalArgumentException(t + " isn't the same as " + get());
            }
        }
    }

    protected boolean valuesEqual(T me, T other) {
        return Objects.equals(me, other);
    }

}
