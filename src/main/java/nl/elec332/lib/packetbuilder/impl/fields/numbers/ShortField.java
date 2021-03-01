package nl.elec332.lib.packetbuilder.impl.fields.numbers;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractNumberField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class ShortField extends AbstractNumberField<Short> {

    public ShortField(ValueReference<Short> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeShort(get());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        set(buffer.readShort());
    }

    @Override
    public int getObjectSize() {
        return 2;
    }

}