package nl.elec332.lib.packetbuilder.internal;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.AbstractWrappedField;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Created by Elec332 on 2/27/2021
 */
public final class WrappedReflectionField extends AbstractWrappedField<AbstractField> {

    public WrappedReflectionField(AbstractField field, Object instance, Field value) {
        super(field);
        this.instance = instance;
        try {
            this.valueGetter = MethodHandles.lookup().unreflectGetter(value);
            if (Modifier.isFinal(value.getModifiers())) {
                this.valueSetter = null;
            } else {
                this.valueSetter = MethodHandles.lookup().unreflectSetter(value);
            }
            setValue();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private final Object instance;
    private final MethodHandle valueGetter;
    private final MethodHandle valueSetter;

    @Override
    public void serialize(ByteBuf buffer) {
        setValue();
        super.serialize(buffer);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        super.deserialize(buffer);
        setValueToRoot();
    }

    @Override
    public int getObjectSize() {
        return super.getObjectSize();
    }

    private void setValueToRoot() {
        if (valueSetter == null) {
            Object o;
            try {
                o = valueGetter.invoke(instance);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
            if (!Objects.equals(o, super.getValue())) {
                throw new IllegalStateException("Values do not match! " + super.getValue() + " should be " + o);
            }
        } else {
            try {
                valueSetter.invoke(instance, super.getValue());
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private void setValue() {
        try {
            super.setValue(valueGetter.invoke(instance));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    protected void importFrom(AbstractField other) {
        setValue();
        super.importFrom(other);
    }

    @Override
    protected boolean canImport(AbstractField other) {
        setValue();
        return super.canImport(other);
    }

    @Override
    public Object getValue() {
        setValue();
        return super.getValue();
    }

    @Override
    protected void setValue(Object value) {
        setValue();
        super.setValue(value);
        setValueToRoot();
    }

    @Override
    protected Class<?> getType() {
        setValue();
        return super.getType();
    }

    @Override
    public String toString() {
        setValue();
        return super.toString();
    }

    @Override
    public int hashCode() {
        setValue();
        return super.hashCode();
    }

    @Override
    protected boolean fieldsEqual(AbstractField other) {
        setValue();
        return getRoot().equals(other);
    }

}
