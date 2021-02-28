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
        root.deserialize(buffer);
        if (bytes.length != buffer.readerIndex()) {
            throw new RuntimeException();
        }
        return root;
    }

}
