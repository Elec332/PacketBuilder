package nl.elec332.lib.packetbuilder.impl.fields.arrays.dynamic;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class DynamicShortArrayField extends AbstractSimpleField<short[]> {

    public DynamicShortArrayField(ValueReference<short[]> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        short[] value = get();
        buffer.writeByte(value.length);
        for (short item : value) {
            buffer.writeShort(item);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        short[] value = new short[buffer.readUnsignedByte()];
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readShort();
        }
        set(value);
    }

    @Override
    public int getObjectSize() {
        return get().length * 2 + 1;
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

}
