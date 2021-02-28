package nl.elec332.lib.packetbuilder.impl.fields.numbers;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractNumberField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class UnsignedByteField extends AbstractNumberField<Integer> {

    public UnsignedByteField(IValueReference<Integer> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeByte(get() & 0xff);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        set((int) buffer.readUnsignedByte());
    }

    @Override
    public int getObjectSize() {
        return 1;
    }

}