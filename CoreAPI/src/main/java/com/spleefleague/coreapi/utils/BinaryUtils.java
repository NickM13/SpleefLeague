package com.spleefleague.coreapi.utils;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class BinaryUtils {

    public static byte[] intToBytes(int l) {
        byte[] result = new byte[4];
        for (int i = 3; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 4;
        }
        return result;
    }

    public static int bytesToInt(final byte[] bytes) {
        int result = 0;
        for (int i = 0; i < Integer.BYTES; i++) {
            result <<= Integer.BYTES;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static int bytesToInt(final byte[] bytes, final int offset) {
        int result = 0;
        for (int i = offset; i < Integer.BYTES + offset; i++) {
            result <<= Integer.BYTES;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

}
