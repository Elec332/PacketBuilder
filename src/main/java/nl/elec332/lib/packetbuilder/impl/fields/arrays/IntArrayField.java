package nl.elec332.lib.packetbuilder.impl.fields.arrays;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class IntArrayField extends AbstractSimpleField<int[]> {

    public IntArrayField(ValueReference<int[]> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        int[] value = get();
        for (int item : value) {
            buffer.writeInt(item);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        int[] value = get();
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readInt();
        }
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