package nl.elec332.lib.packetbuilder.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 2/26/2021
 */
public class PacketEncoder {

    public static <T extends AbstractPacketObject> byte[] encode(T root, Consumer<T> modifier) {
        modifier.accept(root);
        ByteBuf buffer = Unpooled.buffer();
        root.serialize(buffer);
        byte[] ret = new byte[buffer.readableBytes()];
        buffer.readBytes(ret);
        return ret;
    }

}
