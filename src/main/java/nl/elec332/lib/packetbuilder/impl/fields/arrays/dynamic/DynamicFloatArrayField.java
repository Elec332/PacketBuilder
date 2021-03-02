package nl.elec332.lib.packetbuilder.impl.fields.arrays.dynamic;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class DynamicFloatArrayField extends AbstractSimpleField<float[]> {

    public DynamicFloatArrayField(ValueReference<float[]> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        float[] value = get();
        buffer.writeByte(value.length);
        for (float item : value) {
            buffer.writeFloat(item);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        float[] value = new float[buffer.readUnsignedByte()];
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readFloat();
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