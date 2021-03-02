package nl.elec332.lib.packetbuilder.impl.fields.arrays.sized;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IntReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractVarLengthField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class SizedShortArrayField extends AbstractVarLengthField<short[]> {

    public SizedShortArrayField(ValueReference<short[]> reference, IntReference length) {
        super(reference, length);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        short[] value = get();
        for (short item : value) {
            buffer.writeShort(item);
        }
        length.accept(value.length);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        short[] value = new short[length.getAsInt()];
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readShort();
        }
        set(value);
    }

    @Override
    public int getObjectSize() {
        return get().length * 2;
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

}
