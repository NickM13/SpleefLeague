package com.spleefleague.core.chat;

import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import org.bukkit.ChatColor;

import java.util.ArrayList;

/**
 * @author NickM13
 * @since 4/17/2020
 */
public class ChatUtils {
    
    public static String centerText(String message, int centerPos) {
        StringBuilder centered = new StringBuilder();
        
        int msgPxSize = 0;
        boolean prevCode = false;
        boolean isBold = false;
        
        for (char c : message.toCharArray()) {
            if (c == '§') {
                prevCode = true;
            } else if (prevCode == true) {
                prevCode = false;
                isBold = (c == 'l' || c == 'L');
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                int change = (isBold ? dFI.getBoldLength() : dFI.getLength()) + 1;
                msgPxSize += change;
            }
        }
        
        int whitePxSize = (centerPos * 2 - msgPxSize);
        int spaceCount = whitePxSize / 2 / (DefaultFontInfo.SPACE.getLength() + 1);
        
        centered.append(Strings.repeat(' ', spaceCount));
        centered.append(message);
        
        return centered.toString();
    }
    public static String centerTitle(String message) {
        return centerText(message, DefaultFontInfo.SPACE.getLength() * 27);
    }
    
    private static final int DESC_WIDTH = 180;
    
    private static ArrayList<String> wrapDesc(String message) {
        ArrayList<String> msgs = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        StringBuilder word = new StringBuilder();
    
        int msgPxSize = 0;
        boolean prevCode = false;
        boolean isBold = false;
        String prevColor = Chat.DEFAULT;
        
        for (char c : message.toCharArray()) {
            if (c == '§') {
                prevCode = true;
            } else if (prevCode) {
                prevCode = false;
                isBold = (c == 'l' || c == 'L');
                word.append("§").append(c);
                prevColor = ChatColor.getByChar(c) + "";
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                int change = isBold ? dFI.getBoldLength() : dFI.getLength() + 1;
                if (msgPxSize + change > DESC_WIDTH) {
                    msgPxSize = 0;
                    if (line.length() < 2) {
                        line.append(word);
                        word = new StringBuilder();
                    }
                    msgs.add(prevColor + line);
                    line = new StringBuilder();
                }
                msgPxSize += change;
                if (c == ' ') {
                    line.append(word).append(" ");
                    word = new StringBuilder();
                } else {
                    word.append(c);
                }
            }
        }
        if (word.length() > 0) {
            line.append(word);
        }
        if (line.length() > 0) {
            msgs.add(prevColor + line);
        }
        if (msgs.isEmpty()) {
            msgs.add("");
        }
        return msgs;
    }
    
    public static ArrayList<String> wrapDescription(String message) {
        if (message == null || message.equals("")) return Lists.newArrayList("");
        
        ArrayList<String> messageSplit = Lists.newArrayList(message.split("\n"));
        
        ArrayList<String> msgs = new ArrayList<>();
        
        for (String m : messageSplit)
            msgs.addAll(wrapDesc(m));
        
        return msgs;
    }
    
    public static String centerChat(String msg) {
        return centerText(msg, 160);
    }
    
}
