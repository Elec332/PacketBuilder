package nl.elec332.lib.packetbuilder.impl.fields.primitive;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class ShortField extends AbstractField {

    public ShortField(short defaultValue) {
        this.value = defaultValue;
    }

    public short value;

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeShort(value);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        value = buffer.readShort();
    }

    @Override
    public int getObjectSize() {
        return 2;
    }

}