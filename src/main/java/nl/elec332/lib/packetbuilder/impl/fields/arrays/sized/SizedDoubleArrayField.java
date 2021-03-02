package nl.elec332.lib.packetbuilder.impl.fields.arrays.sized;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IntReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractVarLengthField;

import java.util.Arrays;

/**
 * Created by Elec332 on 3/2/2021
 */
public class SizedDoubleArrayField extends AbstractVarLengthField<double[]> {

    public SizedDoubleArrayField(ValueReference<double[]> reference, IntReference length) {
        super(reference, length);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        double[] value = get();
        for (double item : value) {
            buffer.writeDouble(item);
        }
        length.accept(value.length);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        double[] value = new double[length.getAsInt()];
        for (int i = 0; i < value.length; i++) {
            value[i] = buffer.readDouble();
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
