package nl.elec332.lib.packetbuilder.impl.fields.primitive;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;

/**
 * Created by Elec332 on 2/28/2021
 */
public class IntField extends AbstractField {

    public IntField(int defaultValue) {
        this.value = defaultValue;
    }

    public int value;

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeInt(value);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        value = buffer.readInt();
    }

    @Override
    public int getObjectSize() {
        return 4;
    }

}
