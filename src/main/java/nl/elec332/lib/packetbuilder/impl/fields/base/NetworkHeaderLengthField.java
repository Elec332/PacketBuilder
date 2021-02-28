package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractBitField;

/**
 * Created by Elec332 on 2/28/2021
 */
public class NetworkHeaderLengthField extends AbstractBitField {

    public NetworkHeaderLengthField(IValueReference<Number> valueReference, int bits, int bitsStart) {
        super(valueReference, bits, bitsStart);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        set(deserializeValue(buffer) * 4);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        long value = get().longValue();
        if (value % 4 != 0) {
            throw new UnsupportedOperationException(String.valueOf(value));
        }
        serializeValue(value / 4, buffer);
    }

}
