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
        EMOTICONS.put("fingerguns", "ꍜ");
        EMOTICONS.put("heart", "ꍁ");
        EMOTICONS.put("shrug", "¯\\\\(°_o)/¯");
        EMOTICONS.put("what", "ಠ_ಠ");
        EMOTICONS.put("tableflip", "(ノಠ益ಠ)ノ彡┻━┻");
        EMOTICONS.put("cheer", "\\\\(•◡•)/");
    }

    public static Map<String, String> getEmoticons() {
        return EMOTICONS;
    }

}
