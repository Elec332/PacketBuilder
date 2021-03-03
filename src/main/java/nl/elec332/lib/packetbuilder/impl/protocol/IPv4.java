package nl.elec332.lib.packetbuilder.impl.protocol;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.field.RegisteredField;
import nl.elec332.lib.packetbuilder.fields.UnsignedNumberField;
import nl.elec332.lib.packetbuilder.fields.generic.BitsField;
import nl.elec332.lib.packetbuilder.fields.generic.VariableLengthField;
import nl.elec332.lib.packetbuilder.impl.fields.base.InetAddressField;
import nl.elec332.lib.packetbuilder.impl.fields.base.NetworkHeaderLengthField;
import nl.elec332.lib.packetbuilder.impl.fields.numbers.BitValueField;

import java.net.Inet4Address;
import java.util.Arrays;

/**
 * Created by Elec332 on 2/26/2021
 */
public class IPv4 extends AbstractPacketObject {

    public IPv4() {
        super("IPv4");
    }

    private static final int HEADER_LENGTH = 20;

    @RegisteredField
    @BitsField(value = BitValueField.class, startBit = 0, bits = 4)
    public final int version = 4;

    @RegisteredField
    @BitsField(value = NetworkHeaderLengthField.class, bits = 4, startBit = 4)
    public int headerLength = -1;

    @RegisteredField
    @BitsField(value = BitValueField.class, startBit = 0, bits = 6)
    public int dscp = 0;

    @RegisteredField
    @BitsField(value = BitValueField.class, startBit = 6, bits = 2)
    public int ecn = 0;

    @RegisteredField
    @UnsignedNumberField
    public int totalLength = -1;

    @RegisteredField
    @UnsignedNumberField
    public int identification = 7705;

    @RegisteredField
    @BitsField(value = BitValueField.class, startBit = 0, bits = 3)
    public int flags = 0b010;

    @RegisteredField
    @BitsField(value = BitValueField.class, startBit = 3, bits = 13)
    public int fragmentOffset = 0;

    @RegisteredField
    @UnsignedNumberField
    public short ttl = 128;

    @RegisteredField
    @UnsignedNumberField
    public short protocol = -1;

    @RegisteredField
    @UnsignedNumberField
    public int headerChecksum = 0;

    @RegisteredField
    @VariableLengthField(value = InetAddressField.class, length = "version")
    public Inet4Address source;

    @RegisteredField
    @VariableLengthField(value = InetAddressField.class, length = "version")
    public Inet4Address destination;

    @Override
    protected void beforeSerialization(ByteBuf payload) {
        super.beforeSerialization(payload);
        headerLength = getPacketSize();
        totalLength = payload.readableBytes() + headerLength;
    }

    @Override
    protected void afterDeSerialization(ByteBuf buffer) {
        super.afterDeSerialization(buffer);
        if (headerLength != getPacketSize()) {
            throw new RuntimeException();
        }
        if (headerLength + getPayloadLength() != totalLength) {
            throw new RuntimeException();
        }
        if (headerChecksum != 0) {
            int check = 0;
            for (int i = 0; i < HEADER_LENGTH / 2; i++) {
                check += buffer.readUnsignedShort();
            }
            if ((check & 0xffff0000) != 0) {
                check = (check & 0xffff) + (check >> 16);
            }
            if (~((short) check) != 0) {
                throw new RuntimeException("IPv4 checksum failed!");
            }
        }
    }

    @Override
    protected int getPayloadLength() {
        return totalLength - headerLength;
    }

}
