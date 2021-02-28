package nl.elec332.lib.packetbuilder.impl.fields.primitive;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;

/**
 * Created by Elec332 on 2/28/2021
 */
public class UnsignedIntField extends AbstractField {

    public UnsignedIntField(long defaultValue) {
        this.value = defaultValue;
    }

    public long value;

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeInt((int) value);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        value = buffer.readUnsignedInt();
    }

    @Override
    public int getObjectSize() {
        return 4;
    }

}
