package nl.elec332.lib.packetbuilder.impl.fields.arrays.dynamic;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class DynamicIntArrayField extends AbstractSimpleField<int[]> {

    public DynamicIntArrayField(ValueReference<int[]> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        int[] value = get();
        buffer.writeByte(value.length);
        for (int item : value) {
            buffer.writeInt(item);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        int[] value = new int[buffer.readUnsignedByte()];
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readInt();
        }
        set(value);
    }

    @Override
    public int getObjectSize() {
        return get().length * 4 + 1;
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

}