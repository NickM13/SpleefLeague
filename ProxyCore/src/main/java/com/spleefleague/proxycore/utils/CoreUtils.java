package com.spleefleague.proxycore.utils;

import java.util.Collection;

/**
 * @author NickM13
 * @since 9/15/2020
 */
public class CoreUtils {

    public static String mergeSetString(Collection<String> stringCollection) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : stringCollection) {
            stringBuilder.append((stringBuilder.length() > 0 ? ", " : "") + str);
        }
        return stringBuilder.toString();
    }

}
