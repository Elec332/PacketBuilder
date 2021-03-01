package test.test2;

import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.field.RegisteredField;
import nl.elec332.lib.packetbuilder.fields.generic.BitsField;
import nl.elec332.lib.packetbuilder.impl.fields.numbers.BitValueField;

/**
 * Created by Elec332 on 2/27/2021
 */
public class BitTestProt extends AbstractPacketObject {

    public BitTestProt() {
        super("BitTest");
    }

    @RegisteredField
    @BitsField(value = BitValueField.class, bits = 4, startBit = 0)
    public long f1 = 10;

    @RegisteredField
    @BitsField(value = BitValueField.class, bits = 9, startBit = 4)
    public long f2 = 111;

    @RegisteredField
    @BitsField(value = BitValueField.class, bits = 3, startBit = 5)
    public long f3 = 4;

}
