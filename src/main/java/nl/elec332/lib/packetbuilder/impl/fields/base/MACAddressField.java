package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleObjectField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class MACAddressField extends AbstractSimpleObjectField<String> {

    public MACAddressField(String defaultValue) {
        super(defaultValue);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        String[] parts = value.split(":");
        if (parts.length != 6) {
            throw new IllegalArgumentException();
        }
        for (String part : parts) {
            buffer.writeByte(StringUtil.decodeHexByte(part, 0));
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        StringBuilder mac = new StringBuilder(StringUtil.byteToHexStringPadded(buffer.readByte()));
        for (int i = 0; i < 5; i++) {
            mac.append(":");
            mac.append(StringUtil.byteToHexStringPadded(buffer.readByte()));
        }
        this.value = mac.toString();
    }

    @Override
    public int getObjectSize() {
        return 6;
    }

}
