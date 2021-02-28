package nl.elec332.lib.packetbuilder.impl.fields.primitive;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class UnsignedByteField extends AbstractField {

    public UnsignedByteField(int defaultValue) {
        this.value = (short) (defaultValue & 0xff);
    }

    public int value;

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeByte(value & 0xff);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        value = buffer.readUnsignedByte();
    }

    @Override
    public int getObjectSize() {
        return 1;
    }

}