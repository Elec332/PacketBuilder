package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IntReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractVarLengthField;

import java.nio.charset.StandardCharsets;

/**
 * Created by Elec332 on 3/1/2021
 */
public class SizedStringField extends AbstractVarLengthField<String> {

    public SizedStringField(ValueReference<String> reference, IntReference length) {
        super(reference, length);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeBytes(get().getBytes(StandardCharsets.UTF_8));
        length.accept(getObjectSize());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        set(StandardCharsets.UTF_8.decode(buffer.readBytes(length.getAsInt()).nioBuffer()).toString());
    }

    @Override
    public int getObjectSize() {
        return get().length();
    }

}
