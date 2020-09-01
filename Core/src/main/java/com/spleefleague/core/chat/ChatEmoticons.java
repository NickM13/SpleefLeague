package com.spleefleague.core.chat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/29/2020
 */
public class ChatEmoticons {
    
    static Map<String, String> EMOTICONS = new HashMap<>();
    
    static {
        EMOTICONS.put(":fingerguns:", "(☞•◡•)☞");
        EMOTICONS.put(":shrug:", "¯\\\\(°_o)/¯");
        EMOTICONS.put(":what:", "ಠ_ಠ");
        EMOTICONS.put(":tableflip:", "(ノಠ益ಠ)ノ彡┻━┻");
        EMOTICONS.put(":cheer:", "\\\\(•◡•)/");
    }
    
    public static Map<String, String> getEmoticons() {
        return EMOTICONS;
    }
    
}
