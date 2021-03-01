package nl.elec332.lib.packetbuilder.impl.fields.numbers;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractNumberField;

/**
 * Created by Elec332 on 2/28/2021
 */
public class DoubleField extends AbstractNumberField<Double> {

    public DoubleField(ValueReference<Double> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeDouble(get());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        set(buffer.readDouble());
    }

    @Override
    public int getObjectSize() {
        return 8;
    }

}