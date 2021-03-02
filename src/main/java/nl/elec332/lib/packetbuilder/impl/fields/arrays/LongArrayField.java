package nl.elec332.lib.packetbuilder.impl.fields.arrays;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class LongArrayField extends AbstractSimpleField<long[]> {

    public LongArrayField(ValueReference<long[]> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        long[] value = get();
        for (long item : value) {
            buffer.writeLong(item);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        long[] value = get();
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readLong();
        }
    }

    @Override
    public int getObjectSize() {
        return get().length * 8;
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

}