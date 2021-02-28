package nl.elec332.lib.packetbuilder;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.internal.AbstractInternalField;

import java.util.Objects;

/**
 * Created by Elec332 on 2/25/2021
 */
public abstract class AbstractField<T> extends AbstractInternalField<T> {

    @Override
    public abstract void serialize(ByteBuf buffer);

    @Override
    public abstract void deserialize(ByteBuf buffer);

    @Override
    protected boolean isValid() {
        return true;
    }

    @Override
    public abstract int getObjectSize();

    @Override
    @SuppressWarnings("unchecked")
    protected void importFrom(AbstractField<?> other) {
        if (!canImport(other)) {
            throw new UnsupportedOperationException();
        }
        set((T) other.get());
    }

    @Override
    protected boolean canImport(AbstractField<?> other) {
        return other.getClass() == this.getClass();
    }

    @Override
    public abstract T get();

    @Override
    public abstract void accept(T t);

    @Override
    protected AbstractField<?> getRoot() {
        return super.getRoot();
    }

    @Override
    public String toString() {
        try {
            return Objects.toString(get());
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Cannot get value for: " + getClass(), e);
        }
    }

    @Override
    public int hashCode() {
        try {
            return Objects.hashCode(get());
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Cannot get value for: " + getClass(), e);
        }
    }

    @Override
    protected boolean fieldsEqual(AbstractField<?> obj) {
        try {
            return obj.getClass() == this.getClass() && Objects.equals(obj.get(), get());
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Cannot get value for: " + getClass(), e);
        }
    }

}
