package nl.elec332.lib.packetbuilder.api;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.util.ClosableIterator;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 2/28/2021
 */
public interface IPacketBuilder {

    <T extends AbstractPacketObject> T decode(byte[] bytes, T root);

    <T extends AbstractPacketObject> T decode(ByteBuf buffer, T root);

    default <T extends AbstractPacketObject> byte[] encode(T root, Consumer<T> modifier) {
        modifier.accept(root);
        return encode(root);
    }

    <T extends AbstractPacketObject> byte[] encode(T root);

    ClosableIterator<byte[]> readPcapPackets(File file) throws IOException;

    IPacketPayloadManager getPayloadManager();

    IPacketFieldManager getFieldManager();

}
