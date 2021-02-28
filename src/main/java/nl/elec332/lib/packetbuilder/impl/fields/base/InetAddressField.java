package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractVarLengthField;

import java.net.InetAddress;
import java.util.function.IntSupplier;

/**
 * Created by Elec332 on 2/28/2021
 */
public class InetAddressField extends AbstractVarLengthField<InetAddress> {

    public InetAddressField(InetAddress defaultValue, IntSupplier length) {
        super(defaultValue, length);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        byte[] data = value.getAddress();
        if (length.getAsInt() != data.length) {
            throw new RuntimeException();
        }
        buffer.writeBytes(data);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        byte[] data = new byte[length.getAsInt()];
        buffer.readBytes(data);
        try {
            value = InetAddress.getByAddress(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getObjectSize() {
        return length.getAsInt();
    }

}
