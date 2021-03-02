package nl.elec332.lib.packetbuilder.impl.fields.arrays.sized;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IntReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractVarLengthField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class SizedIntArrayField extends AbstractVarLengthField<int[]> {

    public SizedIntArrayField(ValueReference<int[]> reference, IntReference length) {
        super(reference, length);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        int[] value = get();
        for (int item : value) {
            buffer.writeInt(item);
        }
        length.accept(value.length);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        int[] value = new int[length.getAsInt()];
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readInt();
        }
        set(value);
    }

    @Override
    public int getObjectSize() {
        return get().length * 4;
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

}
