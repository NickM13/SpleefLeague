/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.chat;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.database.DBPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import joptsimple.internal.Strings;
import org.bukkit.ChatColor;

/**
 * @author NickM13
 * jack_limestone wuz here
 */
public class Chat {

    private static final HashMap<String, ChatColor> chatColors = new HashMap<>();
    public static String DEFAULT = ChatColor.GRAY + "",
            WHISPER = ChatColor.WHITE + "" + ChatColor.ITALIC + "",
            SUCCESS = ChatColor.GREEN + "",
            INFO = ChatColor.YELLOW + "",
            ERROR = ChatColor.RED + "",
            BROADCAST = ChatColor.LIGHT_PURPLE + "",
            BRACKET = ChatColor.GRAY + "",
            BRACE = ChatColor.DARK_GRAY + "",
            RANK = ChatColor.GRAY + "",
            GAMEMODE = ChatColor.GREEN + "",
            GAMEMAP = ChatColor.RED + "",
            MENU_NAME = ChatColor.WHITE + "" + ChatColor.BOLD,
            MENU_DESC = ChatColor.DARK_GRAY + "",
            TIME = ChatColor.RED + "",
            SCORE = ChatColor.GOLD + "",
            ELO = ChatColor.AQUA + "",
            PLAYER_NAME = ChatColor.YELLOW + "",
            PLAYER_CHAT = ChatColor.WHITE + "",
            PLUGIN_PREFIX = ChatColor.GOLD + "",
            TICKET_PREFIX = ChatColor.GOLD + "",
            TICKET_ISSUE = ChatColor.GREEN + "";

    public static ChatColor getColor(String color) {
        return chatColors.get(color);
    }

    public static void init() {
        chatColors.put("DEFAULT", ChatColor.GRAY);
        chatColors.put("SUCCESS", ChatColor.GREEN);
        chatColors.put("INFO", ChatColor.YELLOW);
        chatColors.put("ERROR", ChatColor.RED);
        ChatChannel.init();
    }
    
    public static String colorize(String msg) {
        String newmsg = "";
        int i;
        for (i = 0; i < msg.length() - 1; i++) {
            if (msg.charAt(i) == '&') {
                switch (msg.charAt(i+1)) {
                    case 'b': newmsg += ChatColor.AQUA; break;
                    case '0': newmsg += ChatColor.BLACK; break;
                    case '9': newmsg += ChatColor.BLUE; break;
                    case '3': newmsg += ChatColor.DARK_AQUA; break;
                    case '1': newmsg += ChatColor.DARK_BLUE; break;
                    case '8': newmsg += ChatColor.DARK_GRAY; break;
                    case '2': newmsg += ChatColor.DARK_GREEN; break;
                    case '5': newmsg += ChatColor.DARK_PURPLE; break;
                    case '4': newmsg += ChatColor.DARK_RED; break;
                    case '6': newmsg += ChatColor.GOLD; break;
                    case '7': newmsg += ChatColor.GRAY; break;
                    case 'a': newmsg += ChatColor.GREEN; break;
                    case 'd': newmsg += ChatColor.LIGHT_PURPLE; break;
                    case 'c': newmsg += ChatColor.RED; break;
                    case 'f': newmsg += ChatColor.WHITE; break;
                    case 'e': newmsg += ChatColor.YELLOW; break;
                    case 'l': newmsg += ChatColor.BOLD; break;
                    case 'i': newmsg += ChatColor.ITALIC; break;
                    case 'r': newmsg += ChatColor.RESET; break;
                    default: newmsg = newmsg.concat(Character.toString(msg.charAt(i))).concat(Character.toString(msg.charAt(i+1))); break;
                }
                i++;
            } else {
                newmsg = newmsg.concat(Character.toString(msg.charAt(i)));
            }
        }
        if (i <= msg.length() - 1) {
            newmsg = newmsg.concat(Character.toString(msg.charAt(msg.length() - 1)));
        }
        return newmsg;
    }

    public static void sendMessage(DBPlayer dbp, String msg) {
        CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
        if (cp.isMuted() == 1) {
            Core.sendMessageToPlayer(dbp, "You're muted!");
            return;
        }
        ChatChannel cc = cp.getChatChannel();
        if (!cc.isAvailable(cp)) {
            cc = ChatChannel.getDefaultChannel();
            cp.setChatChannel(cc);
        }
        if (cp.getOptions().isChannelDisabled(cc.getName())) {
            Core.sendMessageToPlayer(dbp, "You have " + cc.getName() + " muted!");
            Core.sendMessageToPlayer(dbp, "To unmute, go to Menu->Options->Chat Channels");
            return;
        }
        msg = cc.formatMessage(cp, msg);
        if (cp.isMuted() == 2) {
            cp.sendMessage(msg);
        } else {
            for (CorePlayer cp1 : cc.getPlayers(cp)) {
                if (cc.isAvailable(cp1)
                        && !cp1.getOptions().isChannelDisabled(cc.getName())) {
                    cp1.sendMessage(msg);
                }
            }
        }
    }

    public static void sendMessage(ChatChannel channel, String msg) {
        for (CorePlayer cp : Core.getInstance().getPlayers().getOnline()) {
            if (!cp.getOptions().isChannelDisabled(channel.getName())
                    && channel.isAvailable(cp)) {
                cp.sendMessage(msg);
            }
        }
    }

    public static void sendTitle(ChatChannel channel, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (CorePlayer cp : Core.getInstance().getPlayers().getAll()) {
            if (!cp.getOptions().isChannelDisabled(channel.getName())
                    && channel.isAvailable(cp)) {
                cp.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }
        }
    }
    public static void sendTitle(DBPlayer dbp, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        dbp.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendMessageToPlayer(DBPlayer dbp, String msg) {
        if (dbp.isOnline())
            dbp.getPlayer().sendMessage(chatColors.get("DEFAULT") + msg);
    }

    public static void sendMessageToPlayerSuccess(DBPlayer dbp, String msg) {
        if (dbp.isOnline())
            dbp.getPlayer().sendMessage(chatColors.get("SUCCESS") + msg);
    }

    public static void sendMessageToPlayerError(DBPlayer dbp, String msg) {
        if (dbp.isOnline())
            dbp.getPlayer().sendMessage(chatColors.get("ERROR") + msg);
    }

    public static void sendMessageToPlayerInvalid(DBPlayer dbp, String msg) {
        if (dbp.isOnline())
            dbp.getPlayer().sendMessage(chatColors.get("ERROR") + "Invalid command: " + msg);
    }

    public static void sendMessageToPlayerInfo(DBPlayer dbp, String msg) {
        if (dbp.isOnline())
            dbp.getPlayer().sendMessage(chatColors.get("INFO") + msg);
    }

    /**
     *
     * @author SirSpoodles (DefaultFontInfo.java)
     *
     */
    
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
        String line = "", word = "";
        
        int msgPxSize = 0;
        boolean prevCode = false;
        boolean isBold = false;
        String prevColor = Chat.DEFAULT;
        
        for (char c : message.toCharArray()) {
            if (c == '§') {
                prevCode = true;
            } else if (prevCode == true) {
                prevCode = false;
                isBold = (c == 'l' || c == 'L');
                word += "§" + c;
                prevColor = ChatColor.getByChar(c) + "";
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                int change = isBold ? dFI.getBoldLength() : dFI.getLength() + 1;
                if (msgPxSize + change > DESC_WIDTH) {
                    msgPxSize = 0;
                    if (line.length() < 2) {
                        line += word;
                        word = "";
                    }
                    msgs.add(prevColor + line);
                    line = "";
                }
                msgPxSize += change;
                if (c == ' ') {
                    line += word + " ";
                    word = "";
                } else {
                    word += c;
                }
            }
        }
        if (!word.isEmpty()) {
            line += word;
        }
        if (!line.isEmpty()) {
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
    
    public static ArrayList<String> wrapDescription(ArrayList<String> messages) {
        ArrayList<String> msgs = new ArrayList<>();
        
        for (String m : messages) {
            msgs.addAll(wrapDescription(m));
        }
        
        return msgs;
    }
    
    public static String fillTitle(String msg) {
        return centerText(msg, 160);
    }
}
