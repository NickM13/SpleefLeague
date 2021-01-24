/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

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
    
    public static Set<String> enumToStrSet(Class<? extends Enum<?>> clazz, boolean forceLower) {
        return Sets.newHashSet(Arrays.stream(clazz.getEnumConstants()).map(e -> forceLower ? e.name().toLowerCase() : e.name()).toArray(String[]::new));
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
    
    public static TextComponent mergePlayerNames(Collection<CorePlayer> players) {
        TextComponent formatted = new TextComponent();
        Iterator<CorePlayer> cpit = players.iterator();
        boolean first = true;
        while (cpit.hasNext()) {
            CorePlayer cp = cpit.next();
            if (!first) {
                if (!cpit.hasNext()) {
                    formatted.addExtra(" and ");
                } else {
                    formatted.addExtra(", ");
                }
            } else {
                first = false;
            }
            formatted.addExtra(cp.getChatName());
        }
        return formatted;
    }

    /**
     * @deprecated Use StringUtils
     * @param stringCollection
     * @return
     */
    @Deprecated
    public static String mergeSetString(Collection<String> stringCollection) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : stringCollection) {
            stringBuilder.append((stringBuilder.length() > 0 ? ", " : "") + str);
        }
        return stringBuilder.toString();
    }

    public static void knockbackEntity(Entity entity, Vector direction, double power) {
        entity.setVelocity(direction.setY(0).normalize().setY(0.1).multiply(power).add(new Vector(0, 0.1, 0)));
    }
    
}
