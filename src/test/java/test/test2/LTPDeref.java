package test.test2;

import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.field.RegisteredField;
import nl.elec332.lib.packetbuilder.fields.UnsignedNumberField;

/**
 * Created by Elec332 on 3/7/2021
 */
public class LTPDeref extends AbstractPacketObject {

    public LTPDeref() {
        super("LTPDeref");
    }

    @RegisteredField
    @UnsignedNumberField
    public int length = -1;

}
