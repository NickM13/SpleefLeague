/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util;

/**
 * @author NickM13
 */
public class TimeUtils {
    
    private static long times[] = {60, 60, 24, 7, 3, 12};
    private static String names[] = {"sec", "min", "hour", "day", "week", "month", "year"};
    
    /**
     * Greatest common denom time to string
     * Takes milliseconds
     * @param age
     * @return 
    */
    public static String gcdTimeToString(long age) {
        String timeStr = "";
        long now = System.currentTimeMillis();
        boolean future = now < age;
        long seconds = (now - age) / (future ? -1000L : 1000L);
        
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
        String timeStr = "";
        long seconds = age / 1000L;
        
        long time = 1;
        for (int i = 0; i < times.length; i++) {
            if (time > seconds) break;
            long res = (seconds % (time * times[i])) / time;
            timeStr = res + " " + names[i] + (res > 1 ? "s" : "") + (timeStr.length() > 0 ? ", " : " ") + timeStr;
            time *= times[i];
        }
        
        return timeStr;
    }
    
    public static long toMillis(String time) {
        long multiplier = 1000;
        switch (time.substring(time.length() - 1)) {
            case "y": multiplier *= 52;
            case "w": multiplier *= 7;
            case "d": multiplier *= 24;
            case "h": multiplier *= 60;
            case "m": multiplier *= 60;
                break;
        }
        time = time.substring(0, time.length() - 1);
        return Long.valueOf(time) * multiplier;
    }
    
}
