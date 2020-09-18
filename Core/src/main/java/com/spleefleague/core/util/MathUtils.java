package com.spleefleague.core.util;

public class MathUtils {

    public static double cos(double radians, int decimals) {
        return Math.round(Math.cos(radians) * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    public static double sin(double radians, int decimals) {
        return Math.round(Math.sin(radians) * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

}
