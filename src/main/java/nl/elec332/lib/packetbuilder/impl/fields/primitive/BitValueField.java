package nl.elec332.lib.packetbuilder.impl.fields.primitive;

import nl.elec332.lib.packetbuilder.impl.fields.AbstractBitField;

/**
 * Created by Elec332 on 2/27/2021
 */
public class BitValueField extends AbstractBitField {

    public BitValueField(int defaultValue, int bits, int bitsStart) {
        super(defaultValue, bits, bitsStart);
        if (bits > 30) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object getValue() {
        return ((Number) super.getValue()).intValue();
    }

}
