package nl.elec332.lib.packetbuilder.impl.protocol;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.field.RegisteredField;
import nl.elec332.lib.packetbuilder.fields.NumberField;
import nl.elec332.lib.packetbuilder.fields.generic.BitsField;
import nl.elec332.lib.packetbuilder.fields.generic.SimpleConditionalField;
import nl.elec332.lib.packetbuilder.fields.generic.SimpleField;
import nl.elec332.lib.packetbuilder.impl.fields.base.NetworkHeaderLengthField;
import nl.elec332.lib.packetbuilder.impl.fields.numbers.*;

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
    @NumberField
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

//    class TCPOptionsList extends AbstractListField {
//
//        @Override
//        public void serialize(ByteBuf buffer) {
//
//        }
//
//        @Override
//        public void deserialize(ByteBuf buffer) {
//
//        }
//
//        @Override
//        protected int getObjectSize(Object object) {
//            return 0;
//        }
//    }

    static class TCPOption extends AbstractPacketObject {

        public TCPOption() {
            super("TCPOption");
        }

        @RegisteredField
        @SimpleField(ByteField.class)
        public byte kind = 1;

        @RegisteredField
        @SimpleConditionalField(method = "hasLength")
        @SimpleField(UnsignedByteField.class)
        public int length;

        private boolean hasLength() {
            return kind != 1;
        }

    }

    public static class NOPOption extends TCPOption {
    }

    public static class Timestamps extends TCPOption {

        @RegisteredField
        @SimpleField(UnsignedShortField.class)
        public int value = 0;

        @RegisteredField
        @SimpleField(UnsignedShortField.class)
        public int echoReply = 0;

    }

}
