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
public final class WrappedReflectionField extends AbstractWrappedField<Object, AbstractField<Object>> {

    public WrappedReflectionField(AbstractField<Object> field, Object instance, Field value) {
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
            if (!Objects.equals(o, super.get())) {
                throw new IllegalStateException("Values do not match! " + super.get() + " should be " + o);
            }
        } else {
            try {
                valueSetter.invoke(instance, super.get());
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    boolean me = false;

    private void setValue() {
        me = true;
        try {
            super.set(valueGetter.invoke(instance));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        me = false;
    }

    @Override
    protected void importFrom(AbstractField<?> other) {
        setValue();
        super.importFrom(other);
    }

    @Override
    protected boolean canImport(AbstractField<?> other) {
        setValue();
        return super.canImport(other);
    }

    @Override
    public Object get() {
        setValue();
        return super.get();
    }

    @Override
    public void accept(Object o) {
        if (!me) {
            setValue();
            super.accept(o);
            setValueToRoot();
        } else {
            super.accept(o);
        }
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
    protected boolean fieldsEqual(AbstractField<?> other) {
        setValue();
        return getRoot().equals(other);
    }

}
