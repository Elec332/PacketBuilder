package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractBitField;
import nl.elec332.lib.packetbuilder.impl.fields.primitive.BitValueField;

/**
 * Created by Elec332 on 2/28/2021
 */
public class NetworkHeaderLengthField extends AbstractBitField {

    public NetworkHeaderLengthField(int defaultValue, int bits, int bitsStart) {
        super(defaultValue, bits, bitsStart);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        value = deserializeValue(buffer) * 4;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        if (value % 4 != 0) {
            throw new UnsupportedOperationException(String.valueOf(value));
        }
        serializeValue(value / 4, buffer);
    }

    @Override
    public Object getValue() {
        return ((Number) super.getValue()).intValue();
    }

}
