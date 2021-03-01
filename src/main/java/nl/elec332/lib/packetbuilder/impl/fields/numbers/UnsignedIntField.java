package nl.elec332.lib.packetbuilder.impl.fields.numbers;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractNumberField;

/**
 * Created by Elec332 on 2/28/2021
 */
public class UnsignedIntField extends AbstractNumberField<Long> {

    public UnsignedIntField(ValueReference<Long> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeInt((int) get().longValue());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        set(buffer.readUnsignedInt());
    }

    @Override
    public int getObjectSize() {
        return 4;
    }

}
