package nl.elec332.lib.packetbuilder.impl.fields.arrays;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

import java.util.Arrays;

/**
 * Created by Elec332 on 2/26/2021
 */
public class ByteArrayField extends AbstractSimpleField<byte[]> {

    public ByteArrayField(ValueReference<byte[]> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeBytes(get());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        buffer.readBytes(get());
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
