package nl.elec332.lib.packetbuilder.impl.fields.numbers;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractNumberField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class UnsignedShortField extends AbstractNumberField<Integer> {

    public UnsignedShortField(ValueReference<Integer> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeShort(get() & 0xffff);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        set(buffer.readUnsignedShort());
    }

    @Override
    public int getObjectSize() {
        return 2;
    }

}