package com.spleefleague.core.util;

public class SoundUtils {

    public static float calculatePitch(int numUses) {
        return (float) Math.pow(2, (numUses - 12) / 12.);
    }

}
