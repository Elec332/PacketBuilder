package nl.elec332.lib.packetbuilder.impl.protocol;

import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.field.RegisteredField;
import nl.elec332.lib.packetbuilder.fields.SimpleField;
import nl.elec332.lib.packetbuilder.fields.UnsignedNumberField;
import nl.elec332.lib.packetbuilder.impl.fields.base.MACAddressField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class Ethernet extends AbstractPacketObject {

    public Ethernet() {
        super("Ethernet");
    }

    @RegisteredField
    @SimpleField(MACAddressField.class)
    public String destination = "00:00:00:00:00:00";

    @RegisteredField
    @SimpleField(MACAddressField.class)
    public String source = "00:00:00:00:00:00";

    @RegisteredField
    @UnsignedNumberField
    public final int type = 0x0800; //IPv4

}
