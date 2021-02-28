package nl.elec332.lib.packetbuilder.impl.fields.optional;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.AbstractWrappedField;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * Created by Elec332 on 2/28/2021
 */
public class SimpleConditionalField<F extends AbstractField> extends AbstractWrappedField<F> {

    public SimpleConditionalField(F field, BooleanSupplier predicate) {
        super(field);
        this.predicate = Objects.requireNonNull(predicate);
    }

    private final BooleanSupplier predicate;

    @Override
    public void serialize(ByteBuf buffer) {
        if (!predicate.getAsBoolean()) {
            return;
        }
        super.serialize(buffer);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        if (!predicate.getAsBoolean()) {
            return;
        }
        super.deserialize(buffer);
    }

    @Override
    public int getObjectSize() {
        if (!predicate.getAsBoolean()) {
            return 0;
        }
        return super.getObjectSize();
    }

}
