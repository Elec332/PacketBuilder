package nl.elec332.lib.packetbuilder.impl.packet;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;

import java.util.Arrays;

/**
 * Created by Elec332 on 2/26/2021
 */
public class RawPayloadPacket extends AbstractPacketObject {

    public byte[] data;

    public RawPayloadPacket() {
        super("RawPayload");
    }

    @Override
    protected void deserializePayload(ByteBuf buffer) {
        System.out.println(buffer.capacity());
        data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
    }

    @Override
    protected void serializePayload(ByteBuf buffer) {
        buffer.writeBytes(data);
    }

    @Override
    public String toString() {
        return name + "{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}
