/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.coreapi.utils;

/**
 * @author NickM13
 */
public class TimeUtils {
    
    private static long times[] = {60, 60, 24, 7, 3, 12};
    private static String names[] = {"sec", "min", "hour", "day", "week", "month", "year"};
    
    /**
     * Greatest common denom time to string
     * Takes milliseconds
     * @param age Age in millis
     * @return Formatted String
    */
    public static String gcdTimeToString(long age) {
        String timeStr = "";
        long seconds = Math.abs(age) / 1000L;

        // Find biggest unit of time rounded down
        long time = 1;
        for (int i = 0; i < times.length; i++) {
            if (i == times.length - 1 || seconds < time * times[i]) {
                long res = seconds / time;
                timeStr = res + " " + names[i] + (res > 1 ? "s" : "");
                break;
            }
            time *= times[i];
        }
        
        return timeStr;
    }
    
    // Time to string
    public static String timeToString(long age) {
        StringBuilder timeStr = new StringBuilder();
        long seconds = Math.abs(age / 1000L);
        
        long time = 1;
        for (int i = 0; i < times.length; i++) {
            if (time > seconds) break;
            long res = (seconds % (time * times[i])) / time;
            timeStr.insert(0, res + " " + names[i] + (res > 1 ? "s" : "") + (timeStr.length() > 0 ? ", " : " "));
            time *= times[i];
        }
        
        return timeStr.toString();
    }
    
    public static Long toMillis(String time) {
        long multiplier;
        long total = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < time.length(); i++) {
            char c = time.charAt(i);
            if (c >= '0' && c <= '9') {
                builder.append(c);
            } else {
                if (builder.length() > 0) {
                    multiplier = 1;
                    switch (c) {
                        case 'y':
                        case 'Y':
                            multiplier *= 52;
                        case 'w':
                        case 'W':
                            multiplier *= 7;
                        case 'd':
                        case 'D':
                            multiplier *= 24;
                        case 'h':
                        case 'H':
                            multiplier *= 60;
                        case 'm':
                        case 'M':
                            multiplier *= 60;
                        case 's':
                        case 'S':
                            multiplier *= 1000;
                            break;
                        default:
                            return null;
                    }
                    total += Long.parseLong(builder.toString()) * multiplier;
                    builder = new StringBuilder();
                } else {
                    return null;
                }
            }
        }
        if (builder.length() <= 0) return total;
        return total + Long.parseLong(builder.toString()) * 1000;
    }
    
}
