package com.spleefleague.coreapi.chat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class ChatEmoticons {

    static Map<String, String> EMOTICONS = new HashMap<>();

    static {
        EMOTICONS.put("angry", String.valueOf(Character.toChars(21001)));
        EMOTICONS.put("camp", String.valueOf(Character.toChars(21002)));
        EMOTICONS.put("clap", String.valueOf(Character.toChars(21003)));
        EMOTICONS.put("clown", String.valueOf(Character.toChars(21004)));
        EMOTICONS.put("cold", String.valueOf(Character.toChars(21005)));
        EMOTICONS.put("dead", String.valueOf(Character.toChars(21006)));
        EMOTICONS.put("drool", String.valueOf(Character.toChars(21007)));
        EMOTICONS.put("drool_2", String.valueOf(Character.toChars(21008)));
        EMOTICONS.put("flushed", String.valueOf(Character.toChars(21009)));
        EMOTICONS.put("grinning", String.valueOf(Character.toChars(21010)));
        EMOTICONS.put("heart_eyes", String.valueOf(Character.toChars(21011)));
        EMOTICONS.put("imp", String.valueOf(Character.toChars(21012)));
        EMOTICONS.put("kiss", String.valueOf(Character.toChars(21013)));
        EMOTICONS.put("laughing", String.valueOf(Character.toChars(21014)));
        EMOTICONS.put("nauseated", String.valueOf(Character.toChars(21015)));
        EMOTICONS.put("neutral", String.valueOf(Character.toChars(21016)));
        EMOTICONS.put("rage", String.valueOf(Character.toChars(21017)));
        EMOTICONS.put("scream", String.valueOf(Character.toChars(21018)));
        EMOTICONS.put("skull", String.valueOf(Character.toChars(21019)));
        EMOTICONS.put("sleep", String.valueOf(Character.toChars(21020)));
        EMOTICONS.put("sob", String.valueOf(Character.toChars(21021)));
        EMOTICONS.put("star", String.valueOf(Character.toChars(21022)));
        EMOTICONS.put("sunglasses", String.valueOf(Character.toChars(21024)));
        EMOTICONS.put("vomit", String.valueOf(Character.toChars(21025)));
        EMOTICONS.put("wonder", String.valueOf(Character.toChars(21026)));
        EMOTICONS.put("worried", String.valueOf(Character.toChars(21027)));
        EMOTICONS.put("yum", String.valueOf(Character.toChars(21028)));
    }

    public static Map<String, String> getEmoticons() {
        return EMOTICONS;
    }

}
