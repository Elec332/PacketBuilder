package nl.elec332.lib.packetbuilder;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.internal.AbstractInternalField;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created by Elec332 on 2/25/2021
 */
public abstract class AbstractField extends AbstractInternalField {

    public AbstractField() {
        Class<?> type = null;
        MethodHandle valueSetter = null, valueGetter = null;
        try {
            Field value = null;
            Class<?> clazz = getClass();
            while (clazz != Object.class && value == null) {
                try {
                    value = clazz.getDeclaredField("value");
                } catch (Exception e) {
                    clazz = clazz.getSuperclass();
                }
            }
            if (value == null) {
                throw new NoSuchFieldException("value");
            }
            valueGetter = MethodHandles.lookup().unreflectGetter(value);
            valueSetter = MethodHandles.lookup().unreflectSetter(value);
            type = value.getType();
        } catch (Exception e) {
            if (!this.isWrappedField()) {
                throw new RuntimeException("A field must have a value!", e);
            }
        }
        this.type = type;
        this.valueGetter = valueGetter;
        this.valueSetter = valueSetter;
    }

    protected final Class<?> type;
    protected final MethodHandle valueSetter, valueGetter;

    @Override
    public abstract void serialize(ByteBuf buffer);

    @Override
    public abstract void deserialize(ByteBuf buffer);

    @Override
    public abstract int getObjectSize();

    @Override
    protected void importFrom(AbstractField other) {
        if (!canImport(other)) {
            throw new UnsupportedOperationException();
        }
        setValue(other.getValue());
    }

    @Override
    protected boolean canImport(AbstractField other) {
        return other.getClass() == this.getClass();
    }

    @Override
    public Object getValue() {
        try {
            return valueGetter.invoke(this);
        } catch (Throwable t) {
            throw new UnsupportedOperationException(t);
        }
    }

    @Override
    protected void setValue(Object value) {
        try {
            valueSetter.invoke(this, value);
        } catch (Throwable t) {
            throw new UnsupportedOperationException(t);
        }
    }

    @Override
    protected Class<?> getType() {
        return type;
    }

    @Override
    protected AbstractField getRoot() {
        return super.getRoot();
    }

    @Override
    public String toString() {
        try {
            return Objects.toString(getValue());
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Cannot get value for: " + getClass(), e);
        }
    }

    @Override
    public int hashCode() {
        try {
            return Objects.hashCode(getValue());
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Cannot get value for: " + getClass(), e);
        }
    }

    @Override
    protected boolean fieldsEqual(AbstractField obj) {
        try {
            return obj.getClass() == this.getClass() && Objects.equals(obj.getValue(), getValue());
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Cannot get value for: " + getClass(), e);
        }
    }

}
