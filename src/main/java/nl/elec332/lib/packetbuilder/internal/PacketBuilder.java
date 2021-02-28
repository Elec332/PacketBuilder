package nl.elec332.lib.packetbuilder.internal;

import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.IPacketBuilder;
import nl.elec332.lib.packetbuilder.api.IPacketFieldManager;
import nl.elec332.lib.packetbuilder.api.IPacketPayloadManager;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 2/28/2021
 */
public class PacketBuilder implements IPacketBuilder {

    @Override
    public <T extends AbstractPacketObject> T decode(byte[] bytes, T root) {
        return PacketDecoder.decode(bytes, root);
    }

    @Override
    public <T extends AbstractPacketObject> byte[] encode(T root, Consumer<T> modifier) {
        return PacketEncoder.encode(root, modifier);
    }

    @Override
    public IPacketPayloadManager getPayloadManager() {
        return PacketPayloadManager.INSTANCE;
    }

    @Override
    public IPacketFieldManager getFieldManager() {
        return PacketFieldManager.INSTANCE;
    }

}