package nl.elec332.lib.packetbuilder;

import io.netty.buffer.ByteBuf;

import java.util.Objects;

/**
 * Created by Elec332 on 2/27/2021
 */
public class AbstractWrappedField<F extends AbstractField> extends AbstractField {

    public AbstractWrappedField(F field) {
        this.field = Objects.requireNonNull(field);
    }

    protected final F field;

    @Override
    public void serialize(ByteBuf buffer) {
        field.serialize(buffer);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        field.deserialize(buffer);
    }

    @Override
    public int getObjectSize() {
        return field.getObjectSize();
    }

    @Override
    protected void importFrom(AbstractField other) {
        field.importFrom(other);
    }

    @Override
    protected boolean canImport(AbstractField other) {
        return field.canImport(other);
    }

    @Override
    public Object getValue() {
        return field.getValue();
    }

    @Override
    protected void setValue(Object value) {
        field.setValue(value);
    }

    @Override
    protected Class<?> getType() {
        return field.getType();
    }

    @Override
    protected boolean isWrappedField() {
        return true;
    }

    @Override
    protected AbstractField getRoot() {
        return field.getRoot();
    }

    @Override
    public String toString() {
        return field.toString();
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }

    @Override
    protected boolean fieldsEqual(AbstractField obj) {
        return field.fieldsEqual(obj);
    }

}
