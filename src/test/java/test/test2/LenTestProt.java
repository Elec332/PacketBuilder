package test.test2;

import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.field.RegisteredField;
import nl.elec332.lib.packetbuilder.fields.SimpleField;
import nl.elec332.lib.packetbuilder.fields.generic.VariableLengthField;
import nl.elec332.lib.packetbuilder.impl.fields.base.ObjectField;
import nl.elec332.lib.packetbuilder.impl.fields.base.SizedStringField;

/**
 * Created by Elec332 on 3/7/2021
 */
public class LenTestProt extends AbstractPacketObject {

    public LenTestProt() {
        super("LenTestProt");
    }

    @RegisteredField
    @SimpleField(ObjectField.class)
    public LTPDeref length = new LTPDeref();

    @RegisteredField
    @VariableLengthField(value = SizedStringField.class, length = "length.length")
    public String value;

}
