package com.spleefleague.core.util.packet;

/**
 * Only valid in 1.15.2, 1.16 no longer shares data at ends of longs
 *
 * @author Jonas
 */
public class ProtocolLongArrayBitReader {

    private final byte[] data;
    private int offset = 0;

    public ProtocolLongArrayBitReader(byte[] data) {
        this.data = data;
    }

    public short readShort(int bits) {
        short value = 0;
        int read = 0;
        while (bits > 0) {
            int toRead = Math.min(8, bits);
            int b = Byte.toUnsignedInt(readByte(toRead));
            b <<= read;
            value |= b;
            read += toRead;
            bits -= toRead;
        }
        return value;
    }

    public byte readByte(int bits) {
        int p = offset / 8, o = offset % 8;
        p = 7 - p % 8 + (p / 8) * 8;
        byte b = (byte) (Byte.toUnsignedInt(data[p]) >>> o);
        int read = 8 - o;
        if (read > bits) {
            short ff = 0xFF;
            ff >>>= (8 - bits);
            b &= ff;
            offset += bits;
            return b;
        }
        offset += read;
        bits -= read;
        if (bits > 0) {
            byte c = readByte(bits);
            b |= (c << read);
        }
        return b;
    }
}
