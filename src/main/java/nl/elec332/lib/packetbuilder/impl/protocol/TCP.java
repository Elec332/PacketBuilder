package nl.elec332.lib.packetbuilder.impl.protocol;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.field.HiddenField;
import nl.elec332.lib.packetbuilder.api.field.RegisteredField;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.fields.NumberField;
import nl.elec332.lib.packetbuilder.fields.SimpleField;
import nl.elec332.lib.packetbuilder.fields.UnsignedNumberField;
import nl.elec332.lib.packetbuilder.fields.generic.BitsField;
import nl.elec332.lib.packetbuilder.fields.generic.SimpleConditionalField;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSizedOptionsList;
import nl.elec332.lib.packetbuilder.impl.fields.base.NetworkHeaderLengthField;
import nl.elec332.lib.packetbuilder.impl.fields.numbers.BitValueField;

import java.util.Collections;
import java.util.List;

/**
 * Created by Elec332 on 2/28/2021
 */
public class TCP extends AbstractPacketObject {

    public TCP() {
        super("TCP");
    }

    private static final int HEADER_LENGTH = 20;

    @RegisteredField
    @UnsignedNumberField
    public int sourcePort = 60511;

    @RegisteredField
    @UnsignedNumberField
    public int destinationPort = 198;

    @RegisteredField
    @NumberField
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
    @UnsignedNumberField
    public int windowSize = 0x1000;

    @RegisteredField
    @UnsignedNumberField
    public int checksum = 0;

    @RegisteredField
    @UnsignedNumberField
    public int urgentPointer = 0;

    @RegisteredField
    @SimpleField(TCPOptionsList.class)
    public List<TCPOption> options = Collections.emptyList();

    @Override
    protected void beforeSerialization(ByteBuf payload) {
        super.beforeSerialization(payload);
        headerLength = getPacketSize();
    }

    @Override
    protected void afterDeSerialization(ByteBuf buffer) {
        super.afterDeSerialization(buffer);
        if (headerLength != getPacketSize()) {
            throw new RuntimeException();
        }
    }

    class TCPOptionsList extends AbstractSizedOptionsList<TCPOption> {

        public TCPOptionsList(ValueReference<List<TCPOption>> reference) {
            super(reference);
        }

        @Override
        protected int expectedListSize() {
            return headerLength - HEADER_LENGTH;
        }

        @Override
        protected TCPOption getNextType(ByteBuf buffer) {
            TCPOption option;
            byte value = buffer.readByte();
            switch (value) {
                case 1 -> option = new NOPOption();
                case 2 -> option = new SegmentSize();
                case 3 -> option = new WindowScale();
                case 4 -> option = new SACKPermitted();
                case 8 -> option = new Timestamps();
                default -> throw new IllegalArgumentException(value + "");
            }
            return option;
        }

    }

    public static class TCPOption extends AbstractPacketObject {

        public TCPOption(String name, int kind) {
            super("TCPOption:" + name);
            this.kind = (byte) kind;
        }

        @HiddenField
        @RegisteredField
        @NumberField
        public final byte kind;

        @HiddenField
        @RegisteredField
        @SimpleConditionalField(method = "hasLength")
        @UnsignedNumberField(Byte.class)
        public int length = 1;

        @Override
        protected void beforeSerialization(ByteBuf payload) {
            super.beforeSerialization(payload);
            length = getPacketSize();
        }

        @Override
        protected void afterDeSerialization(ByteBuf buffer) {
            super.afterDeSerialization(buffer);
            if (length != getPacketSize()) {
                throw new RuntimeException();
            }
        }

        protected boolean hasLength() {
            return true;
        }

    }

    public static class NOPOption extends TCPOption {

        public NOPOption() {
            super("NOP", 1);
        }

        @Override
        protected boolean hasLength() {
            return false;
        }

    }

    public static class SegmentSize extends TCPOption {

        public SegmentSize() {
            super("SegmentSize", 2);
        }

        @RegisteredField
        @UnsignedNumberField
        public int value = 1460;

    }

    public static class WindowScale extends TCPOption {

        public WindowScale() {
            super("WindowScale", 3);
        }

        @RegisteredField
        @UnsignedNumberField
        public short shiftCount = 7;

    }

    public static class SACKPermitted extends TCPOption {

        public SACKPermitted() {
            super("SACKPermitted", 4);
        }

    }

    public static class Timestamps extends TCPOption {

        public Timestamps() {
            super("Timestamp", 8);
        }

        @RegisteredField
        @UnsignedNumberField
        public long value = 123;

        @RegisteredField
        @UnsignedNumberField
        public long echoReply = 34565;

    }

}
