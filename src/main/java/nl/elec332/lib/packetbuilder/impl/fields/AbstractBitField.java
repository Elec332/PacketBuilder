package nl.elec332.lib.packetbuilder.impl.fields;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractField;

/**
 * Created by Elec332 on 2/27/2021
 */
public abstract class AbstractBitField extends AbstractField {

    public AbstractBitField(long defaultValue, int bits, int bitsStart) {
        if (bits > 48) {
            throw new UnsupportedOperationException();
        }
        if (bitsStart < 0 || bitsStart > 7) {
            throw new IllegalArgumentException();
        }
        this.value = defaultValue;
        this.totalBits = bits + bitsStart;
        this.bitsStart = bitsStart;
        long mask = 0;
        int i = 0;
        for (; i < Math.floorDiv(bits, 8); i++) {
            mask |= 0xffL << (i * 8);
        }
        if (Math.floorDiv(bits, 8) != 0) {
            i++;
        }
        mask |= (long) BIT_PADDING[bits % 8] << (i * 8);
        this.mask = mask;
    }

    private static final int[] BIT_PADDING = new int[]{
            0b0, 0b1, 0b11, 0b111, 0b1111, 0b11111, 0b111111, 0b1111111, 0b11111111
    };

    public long value;
    private final long mask;
    private final int totalBits;
    private final int bitsStart;

    @Override
    public void serialize(ByteBuf buffer) {
        serializeValue(value, buffer);
    }

    protected void serializeValue(long value, ByteBuf buffer) {
        int totByt = getFullBytes() + getExtraByte();
        long write = (value & mask) << totByt * 8 - totalBits;
        for (int i = 0; i < totByt; i++) {
            byte dat = (byte) ((write >> (totByt - i - 1) * 8) & 0xff);
            if (bitsStart != 0 && i == 0) {
                byte preDat = buffer.getByte(buffer.writerIndex() - 1);
                buffer.setByte(buffer.writerIndex() - 1, preDat | dat);
            } else {
                buffer.writeByte(dat);
            }
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        value = deserializeValue(buffer);
    }

    protected long deserializeValue(ByteBuf buffer) {
        int val = 0;
        int bytesRead = getFullBytes();
        int extra = getExtraByte();
        for (int i = 0; i < bytesRead; i++) {
            val |= (Byte.toUnsignedLong(buffer.readByte()) << (((bytesRead + extra) * 8) - ((i + 1) * 8)));
        }
        if (extra > 0) {
            val |= Byte.toUnsignedInt(buffer.slice().readByte());
        }
        val >>= ((bytesRead + extra) * 8 - totalBits);
        val &= mask;
        return val;
    }

    private int getFullBytes() {
        return Math.floorDiv(totalBits, 8);
    }

    private int getExtraByte() {
        return totalBits % 8 == 0 ? 0 : 1;
    }

    @Override
    public int getObjectSize() {
        return getFullBytes();
    }

}
