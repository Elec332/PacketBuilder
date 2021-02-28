package nl.elec332.lib.packetbuilder.api;

import io.netty.buffer.ByteBuf;

/**
 * Created by Elec332 on 2/27/2021
 */
public interface ISerializableObject {

    void serialize(ByteBuf buffer);

    void deserialize(ByteBuf buffer);

    int getObjectSize();

}
