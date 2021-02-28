package nl.elec332.lib.packetbuilder.impl.packet;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;

/**
 * Created by Elec332 on 2/26/2021
 */
public class NoPayloadPacket extends AbstractPacketObject {

    public NoPayloadPacket() {
        super("NoPayload");
    }

    @Override
    protected void deserializePayload(ByteBuf buffer) {
    }

    @Override
    public void addPayload(AbstractPacketObject data) {
        throw new UnsupportedOperationException("NoPayload cannot have a payload...");
    }

    @Override
    public <T extends AbstractPacketObject> T getPayload(Class<T> payloadType) {
        return null;
    }

    @Override
    public String toString() {
        return "None";
    }

}
