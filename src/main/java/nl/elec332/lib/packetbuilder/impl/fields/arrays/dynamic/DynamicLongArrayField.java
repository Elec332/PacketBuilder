package nl.elec332.lib.packetbuilder.impl.fields.arrays.dynamic;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class DynamicLongArrayField extends AbstractSimpleField<long[]> {

    public DynamicLongArrayField(ValueReference<long[]> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        long[] value = get();
        buffer.writeByte(value.length);
        for (long item : value) {
            buffer.writeLong(item);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        long[] value = new long[buffer.readUnsignedByte()];
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readLong();
        }
        set(value);
    }

    @Override
    public int getObjectSize() {
        return get().length * 8 + 1;
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

}
