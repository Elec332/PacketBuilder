package nl.elec332.lib.packetbuilder.internal;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.api.ISerializableObject;

/**
 * Created by Elec332 on 2/26/2021
 */
public abstract class AbstractInternalField implements ISerializableObject {

    @Override
    public abstract void serialize(ByteBuf buffer);

    @Override
    public abstract void deserialize(ByteBuf buffer);

    @Override
    public abstract int getObjectSize();

    protected abstract void importFrom(AbstractField other);

    protected abstract boolean canImport(AbstractField other);

    protected abstract Object getValue();

    protected abstract void setValue(Object value);

    protected abstract Class<?> getType();

    protected boolean isWrappedField() {
        return false;
    }

    protected AbstractField getRoot() {
        return (AbstractField) this;
    }

    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractInternalField && fieldsEqual(((AbstractInternalField) obj).getRoot());
    }

    protected abstract boolean fieldsEqual(AbstractField other);

}
