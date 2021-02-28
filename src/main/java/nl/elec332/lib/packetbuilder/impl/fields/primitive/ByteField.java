package nl.elec332.lib.packetbuilder.impl.fields.primitive;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class ByteField extends AbstractField {

    public ByteField(byte defaultValue) {
        this.value = defaultValue;
    }

    public byte value;

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeByte(value);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        value = buffer.readByte();
    }


    @Override
    public int getObjectSize() {
        return 1;
    }

}
