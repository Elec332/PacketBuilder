package nl.elec332.lib.packetbuilder.impl.fields.arrays.sized;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IntReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractVarLengthField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class SizedLongArrayField extends AbstractVarLengthField<long[]> {

    public SizedLongArrayField(ValueReference<long[]> reference, IntReference length) {
        super(reference, length);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        long[] value = get();
        for (long item : value) {
            buffer.writeLong(item);
        }
        length.accept(value.length);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        long[] value = new long[length.getAsInt()];
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readLong();
        }
        set(value);
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
