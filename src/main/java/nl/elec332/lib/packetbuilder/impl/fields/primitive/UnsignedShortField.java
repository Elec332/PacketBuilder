package nl.elec332.lib.packetbuilder.impl.fields.primitive;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class UnsignedShortField extends AbstractField {

    public UnsignedShortField(int defaultValue) {
        this.value = defaultValue;
    }

    public int value;

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeShort(value & 0xffff);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        value = buffer.readUnsignedShort();
    }

    @Override
    public int getObjectSize() {
        return 2;
    }

}