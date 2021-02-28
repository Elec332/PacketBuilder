package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleObjectField;

import java.util.Arrays;

/**
 * Created by Elec332 on 2/26/2021
 */
public class ByteArrayField extends AbstractSimpleObjectField<byte[]> {

    public ByteArrayField(byte[] array) {
        super(array);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeBytes(value);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        buffer.readBytes(value);
    }

    @Override
    public int getObjectSize() {
        return value.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(value);
    }

}
