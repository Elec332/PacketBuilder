package nl.elec332.lib.packetbuilder.impl.protocol;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.field.BitsField;
import nl.elec332.lib.packetbuilder.api.util.RegisteredField;
import nl.elec332.lib.packetbuilder.api.field.SimpleField;
import nl.elec332.lib.packetbuilder.impl.fields.base.NetworkHeaderLengthField;
import nl.elec332.lib.packetbuilder.impl.fields.primitive.BitValueField;
import nl.elec332.lib.packetbuilder.impl.fields.primitive.IntField;
import nl.elec332.lib.packetbuilder.impl.fields.primitive.UnsignedShortField;

/**
 * Created by Elec332 on 2/28/2021
 */
public class TCP extends AbstractPacketObject {

    public TCP() {
        super("TCP");
    }

    @RegisteredField
    @SimpleField(UnsignedShortField.class)
    public int sourcePort = 60511;

    @RegisteredField
    @SimpleField(UnsignedShortField.class)
    public int destinationPort = 198;

    @RegisteredField
    @SimpleField(IntField.class)
    public int seq = 0;

    @RegisteredField
    @SimpleField(IntField.class)
    public int ack = 0;

    @RegisteredField
    @BitsField(value = NetworkHeaderLengthField.class, bits = 4, startBit = 0)
    public int headerLength = -1;

    @RegisteredField
    @BitsField(value = BitValueField.class, startBit = 4, bits = 3)
    public final int reserved = 0;

    @RegisteredField
    @BitsField(value = BitValueField.class, startBit = 7, bits = 9)
    public int flags = 0;

    @RegisteredField
    @SimpleField(UnsignedShortField.class)
    public int windowSize = 0x1000;

    @RegisteredField
    @SimpleField(UnsignedShortField.class)
    public int checksum = 0;

    @RegisteredField
    @SimpleField(UnsignedShortField.class)
    public int urgentPointer = 0;

    @Override
    protected void beforeSerialization(ByteBuf payload) {
        super.beforeSerialization(payload);
        headerLength = getPacketSize();
    }

    @Override
    protected void afterDeSerialization() {
        super.afterDeSerialization();
        if (headerLength != getPacketSize()) {
            throw new RuntimeException();
        }
    }

}
