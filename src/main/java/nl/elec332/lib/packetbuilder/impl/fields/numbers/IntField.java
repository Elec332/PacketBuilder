package nl.elec332.lib.packetbuilder.impl.fields.numbers;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractNumberField;

/**
 * Created by Elec332 on 2/28/2021
 */
public class IntField extends AbstractNumberField<Integer> {

    public IntField(IValueReference<Integer> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeInt(get());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        set(buffer.readInt());
    }

    @Override
    public int getObjectSize() {
        return 4;
    }

}
