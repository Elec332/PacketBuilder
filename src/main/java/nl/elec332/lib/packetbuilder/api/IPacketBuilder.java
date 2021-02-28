package nl.elec332.lib.packetbuilder.api;

import nl.elec332.lib.packetbuilder.AbstractPacketObject;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 2/28/2021
 */
public interface IPacketBuilder {

    <T extends AbstractPacketObject> T decode(byte[] bytes, T root);

    <T extends AbstractPacketObject> byte[] encode(T root, Consumer<T> modifier);

    IPacketPayloadManager getPayloadManager();

    IPacketFieldManager getFieldManager();

}
