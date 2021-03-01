package nl.elec332.lib.packetbuilder.impl.fields.numbers;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractNumberField;

/**
 * Created by Elec332 on 2/28/2021
 */
public class FloatField extends AbstractNumberField<Float> {

    public FloatField(ValueReference<Float> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeFloat(get());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        set(buffer.readFloat());
    }

    @Override
    public int getObjectSize() {
        return 4;
    }

}