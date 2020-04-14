/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

/**
 * @author NickM13
 */
public class CoreUtils {
    
    /**
     * Number with suffix (st, nd, rd, th)
     * @param num
     * @return
     */
    public static String getPlaceSuffixed(int num) {
        String str = "" + num;
        if (num > 10 && num < 20) {
            str += "th";
        } else {
            switch (num % 10) {
                case 1: str += "st"; break;
                case 2: str += "nd"; break;
                case 3: str += "rd"; break;
                default: str += "th"; break;
            }
        }
        return str;
    }
    
    public static Set<String> enumToSet(Class<? extends Enum> clazz) {
        return Sets.newHashSet(Arrays.stream(clazz.getEnumConstants()).map(Enum::name).toArray(String[]::new));
    }
    
}
