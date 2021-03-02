package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

import java.nio.charset.StandardCharsets;

/**
 * Created by Elec332 on 3/2/2021
 */
public class DynamicStringField extends AbstractSimpleField<String> {

    public DynamicStringField(ValueReference<String> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        String value = get();
        buffer.writeByte(value.length());
        buffer.writeBytes(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        int length = buffer.readUnsignedByte();
        set(StandardCharsets.UTF_8.decode(buffer.readBytes(length).nioBuffer()).toString());
    }

    @Override
    public int getObjectSize() {
        return get().length() + 1;
    }

}
