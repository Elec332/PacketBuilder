package nl.elec332.lib.packetbuilder.impl.fields.arrays.dynamic;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class DynamicDoubleArrayField extends AbstractSimpleField<double[]> {

    public DynamicDoubleArrayField(ValueReference<double[]> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        double[] value = get();
        buffer.writeByte(value.length);
        for (double item : value) {
            buffer.writeDouble(item);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        double[] value = new double[buffer.readUnsignedByte()];
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readDouble();
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
