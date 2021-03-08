package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IntReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractVarLengthField;

import java.net.InetAddress;

/**
 * Created by Elec332 on 2/28/2021
 */
public class InetAddressField extends AbstractVarLengthField<InetAddress> {

    public InetAddressField(ValueReference<InetAddress> reference, IntReference length) {
        super(reference, length);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        byte[] data = get().getAddress();
        if (getLength() != data.length) {
            throw new RuntimeException();
        }
        buffer.writeBytes(data);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        byte[] data = new byte[getLength()];
        buffer.readBytes(data);
        try {
            set(InetAddress.getByAddress(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getObjectSize() {
        return getLength();
    }

}
