/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util;

import com.google.common.collect.Sets;
import com.spleefleague.core.player.CorePlayer;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author NickM13
 */
public class CoreUtils {
    
    /**
     * Number with suffix (st, nd, rd, th)
     * @param place Place
     * @return Formatted String
     */
    public static String getPlaceSuffixed(int place) {
        String str = "" + place;
        if (place > 10 && place < 20) {
            str += "th";
        } else {
            switch (place % 10) {
                case 1: str += "st"; break;
                case 2: str += "nd"; break;
                case 3: str += "rd"; break;
                default: str += "th"; break;
            }
        }
        return str;
    }
    
    public static Set<String> enumToSet(Class<? extends Enum<?>> clazz) {
        return Sets.newHashSet(Arrays.stream(clazz.getEnumConstants()).map(Enum::name).toArray(String[]::new));
    }
    
    /**
     * Get a private field from an object
     *
     * @param fieldName Field Name
     * @param clazz Class
     * @param object Object
     * @return Field Object
     */
    public static Object getPrivateField(String fieldName, Class<?> clazz, Object object) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String mergePlayerNames(Collection<CorePlayer> players) {
        StringBuilder formatted = new StringBuilder();
        Iterator<CorePlayer> cpit = players.iterator();
        while (cpit.hasNext()) {
            CorePlayer cp = cpit.next();
            if (formatted.length() > 0) {
                if (!cpit.hasNext()) {
                    formatted.append(" and ");
                } else {
                    formatted.append(", ");
                }
            }
            formatted.append(cp.getDisplayName());
        }
        return formatted.toString();
    }
    
}
