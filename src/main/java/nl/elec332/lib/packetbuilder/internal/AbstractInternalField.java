package nl.elec332.lib.packetbuilder.internal;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.api.ISerializableObject;
import nl.elec332.lib.packetbuilder.api.util.IValueReference;

/**
 * Created by Elec332 on 2/26/2021
 */
public abstract class AbstractInternalField<T> implements ISerializableObject, IValueReference<T> {

    @Override
    public abstract void serialize(ByteBuf buffer);

    @Override
    public abstract void deserialize(ByteBuf buffer);

    @Override
    public abstract int getObjectSize();

    protected abstract void importFrom(AbstractField<?> other);

    protected abstract boolean canImport(AbstractField<?> other);

    @Override
    public abstract T get();

    public final void set(T t) {
        accept(t);
    }

    @Override
    public abstract void accept(T t);

    protected AbstractField<?> getRoot() {
        return (AbstractField<?>) this;
    }

    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        return obj instanceof AbstractInternalField && fieldsEqual(((AbstractInternalField) obj).getRoot());
    }

    protected abstract boolean fieldsEqual(AbstractField<?> other);

}
