package nl.elec332.lib.packetbuilder.impl.fields.arrays.dynamic;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class DynamicByteArrayField extends AbstractSimpleField<byte[]> {

    public DynamicByteArrayField(ValueReference<byte[]> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        byte[] value = get();
        buffer.writeByte(value.length);
        buffer.writeBytes(value);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        byte[] value = new byte[buffer.readUnsignedByte()];
        buffer.readBytes(value);
        set(value);
    }

    @Override
    public int getObjectSize() {
        return get().length + 1;
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

}