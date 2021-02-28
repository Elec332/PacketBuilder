package nl.elec332.lib.packetbuilder.impl.fields.numbers;

import nl.elec332.lib.packetbuilder.api.util.IValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractBitField;

/**
 * Created by Elec332 on 2/27/2021
 */
public class BitValueField extends AbstractBitField {

    public BitValueField(IValueReference<Number> reference, int bits, int bitsStart) {
        super(reference, bits, bitsStart);
        if (bits > 30) {
            throw new UnsupportedOperationException();
        }
    }

}
