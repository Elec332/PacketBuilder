package nl.elec332.lib.packetbuilder.impl.fields.arrays.sized;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IntReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractVarLengthField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class SizedByteArrayField extends AbstractVarLengthField<byte[]> {

    public SizedByteArrayField(ValueReference<byte[]> reference, IntReference length) {
        super(reference, length);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        byte[] value = get();
        buffer.writeBytes(value);
        setLength(value.length);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        byte[] value = new byte[getLength()];
        buffer.readBytes(value);
        set(value);
    }

    @Override
    public int getObjectSize() {
        return get().length;
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

}
