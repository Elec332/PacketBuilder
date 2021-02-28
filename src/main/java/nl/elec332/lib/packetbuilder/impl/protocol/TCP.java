package nl.elec332.lib.packetbuilder.impl.protocol;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.field.RegisteredField;
import nl.elec332.lib.packetbuilder.api.util.IValueReference;
import nl.elec332.lib.packetbuilder.fields.NumberField;
import nl.elec332.lib.packetbuilder.fields.SimpleField;
import nl.elec332.lib.packetbuilder.fields.UnsignedNumberField;
import nl.elec332.lib.packetbuilder.fields.generic.BitsField;
import nl.elec332.lib.packetbuilder.fields.generic.SimpleConditionalField;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractListField;
import nl.elec332.lib.packetbuilder.impl.fields.base.NetworkHeaderLengthField;
import nl.elec332.lib.packetbuilder.impl.fields.numbers.BitValueField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Elec332 on 2/28/2021
 */
public class TCP extends AbstractPacketObject {

    public TCP() {
        super("TCP");
    }

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
    protected void afterDeSerialization() {
        super.afterDeSerialization();
        if (headerLength != getPacketSize()) {
            throw new RuntimeException();
        }
    }

    class TCPOptionsList extends AbstractListField<TCPOption> {

        public TCPOptionsList(IValueReference<List<TCPOption>> reference) {
            super(reference);
        }

        @Override
        public void serialize(ByteBuf buffer) {
            for (TCPOption o : get()) {
                o.serialize(buffer);
            }
        }

        @Override
        public void deserialize(ByteBuf buffer) {
            int count = 0;
            int max = headerLength - 20;
            set(new ArrayList<>());
            while (max - count > 0) {
                TCPOption option;
                switch (buffer.slice().readByte()) {
                    case 1 -> option = new NOPOption();
                    case 8 -> option = new Timestamps();
                    default -> throw new IllegalArgumentException();
                }
                option.deserialize(buffer);
                count += option.length;
                add(option);
            }
        }

        @Override
        public int getObjectSize() {
            return get().stream().mapToInt(AbstractPacketObject::getObjectSize).sum();
        }

    }

    public static class TCPOption extends AbstractPacketObject {

        public TCPOption(String name, int kind) {
            super("TCPOption:" + name);
            this.kind = (byte) kind;
        }

        @RegisteredField
        @NumberField
        public final byte kind;

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
        protected void afterDeSerialization() {
            super.afterDeSerialization();
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
