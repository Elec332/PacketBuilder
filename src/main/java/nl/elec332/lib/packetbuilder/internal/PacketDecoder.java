package nl.elec332.lib.packetbuilder.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;

/**
 * Created by Elec332 on 2/26/2021
 */
public class PacketDecoder {

    public static <T extends AbstractPacketObject> T decode(byte[] bytes, T root) {
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes).asReadOnly();
        return decode(buffer, root);
    }

    public static <T extends AbstractPacketObject> T decode(ByteBuf buffer, T root) {
        root.deserialize(buffer);
        if (buffer.capacity() != buffer.readerIndex()) {
            throw new RuntimeException("Not all bytes were read. Bytes provided: " + buffer.capacity() + "  Bytes read: " + buffer.readerIndex());
        }
        return root;
    }

}
