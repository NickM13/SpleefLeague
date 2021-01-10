/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.chat;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;

import java.util.*;
import java.util.function.BiConsumer;

import com.spleefleague.core.request.ConsoleRequest;
import com.spleefleague.core.request.PlayerRequest;
import com.spleefleague.core.request.RequestManager;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.bungee.PacketChatBungee;
import com.spleefleague.coreapi.utils.packet.spigot.PacketChatSpigot;
import com.spleefleague.coreapi.utils.packet.spigot.PacketTellSpigot;
import org.bukkit.Sound;

/**
 * @author NickM13
 * jack_limestone wuz here
 * Vidiot wuz here two
 */
public class Chat {

    private static final HashMap<String, ChatColor> chatColors = new HashMap<>();
    public static String UNDO = ChatColor.UNDO + "",
            DEFAULT = ChatColor.GRAY + "",
            WHISPER = ChatColor.WHITE + "" + ChatColor.ITALIC + "",
            SUCCESS = ChatColor.GREEN + "",
            INFO = ChatColor.YELLOW + "",
            ERROR = ChatColor.RED + "",
            BROADCAST = ChatColor.LIGHT_PURPLE + "",
            BRACKET = ChatColor.GRAY + "",
            TAG_BRACE = ChatColor.DARK_GRAY + "",
            RANK = ChatColor.GRAY + "",
            GAMEMODE = ChatColor.GREEN + "",
            GAMEMAP = ChatColor.RED + "",
            MENU_NAME = ChatColor.WHITE + "" + ChatColor.BOLD,
            DESCRIPTION = ChatColor.GRAY + "",
            STAT = ChatColor.RED + "",
            TIME = ChatColor.RED + "",
            SCORE = ChatColor.GOLD + "",
            ELO = ChatColor.AQUA + "",
            PLAYER_NAME = ChatColor.YELLOW + "",
            PLAYER_CHAT = ChatColor.WHITE + "",
            TAG = ChatColor.GOLD + "",
            TICKET_PREFIX = ChatColor.GOLD + "",
            TICKET_ISSUE = ChatColor.GREEN + "",
            SCOREBOARD_DEFAULT = ChatColor.WHITE + "";

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

    /**
     * Replaces all &# with their associated colors and \n with newlines
     *
     * @param msg String to Colorize
     * @return Colorized String
     */
    public static String colorize(String msg) {
        StringBuilder newmsg = new StringBuilder();
        int i;
        Stack<ChatColor> colorStack = new Stack<>();
        for (i = 0; i < msg.length() - 1; i++) {
            if (msg.charAt(i) == '§') {
                switch (msg.charAt(i+1)) {
                    case 'b': newmsg.append(colorStack.push(ChatColor.AQUA)); break;
                    case '0': newmsg.append(colorStack.push(ChatColor.BLACK)); break;
                    case '9': newmsg.append(colorStack.push(ChatColor.BLUE)); break;
                    case '3': newmsg.append(colorStack.push(ChatColor.DARK_AQUA)); break;
                    case '1': newmsg.append(colorStack.push(ChatColor.DARK_BLUE)); break;
                    case '8': newmsg.append(colorStack.push(ChatColor.DARK_GRAY)); break;
                    case '2': newmsg.append(colorStack.push(ChatColor.DARK_GREEN)); break;
                    case '5': newmsg.append(colorStack.push(ChatColor.DARK_PURPLE)); break;
                    case '4': newmsg.append(colorStack.push(ChatColor.DARK_RED)); break;
                    case '6': newmsg.append(colorStack.push(ChatColor.GOLD)); break;
                    case '7': newmsg.append(colorStack.push(ChatColor.GRAY)); break;
                    case 'a': newmsg.append(colorStack.push(ChatColor.GREEN)); break;
                    case 'd': newmsg.append(colorStack.push(ChatColor.LIGHT_PURPLE)); break;
                    case 'c': newmsg.append(colorStack.push(ChatColor.RED)); break;
                    case 'f': newmsg.append(colorStack.push(ChatColor.WHITE)); break;
                    case 'e': newmsg.append(colorStack.push(ChatColor.YELLOW)); break;
                    case 'l': newmsg.append(colorStack.push(ChatColor.BOLD)); break;
                    case 'i': newmsg.append(colorStack.push(ChatColor.ITALIC)); break;
                    case 'r': newmsg.append(colorStack.push(ChatColor.RESET)); break;
                    case 'n': newmsg.append(colorStack.push(ChatColor.UNDERLINE)); break;
                    case 'm': newmsg.append(colorStack.push(ChatColor.STRIKETHROUGH)); break;
                    case 'k': newmsg.append(colorStack.push(ChatColor.MAGIC)); break;
                    case 'u':
                        if (colorStack.size() <= 1) {
                            newmsg.append(ChatColor.RESET);
                        } else {
                            colorStack.pop();
                            newmsg.append(colorStack.peek());
                        }
                        break;
                    default:
                        newmsg = new StringBuilder(newmsg.toString().concat(Character.toString(msg.charAt(i))).concat(Character.toString(msg.charAt(i + 1)))); break;
                }
                i++;
            } else if (msg.charAt(i) == '\\') {
                if (msg.charAt(i+1) == 'n') {
                    newmsg.append("\n");
                } else if (msg.charAt(i+1) == '\\') {
                    newmsg.append("\\");
                } else {
                    newmsg = new StringBuilder(newmsg.toString().concat(Character.toString(msg.charAt(i))).concat(Character.toString(msg.charAt(i + 1))));
                }
                i++;
            } else {
                newmsg = new StringBuilder(newmsg.toString().concat(Character.toString(msg.charAt(i))));
            }
        }
        if (i <= msg.length() - 1) {
            newmsg = new StringBuilder(newmsg.toString().concat(Character.toString(msg.charAt(msg.length() - 1))));
        }
        return newmsg.toString();
    }

    public static void sendMessage(CorePlayer cp, String msg, boolean url) {
        if (cp.isMuted() == 1) {
            Core.getInstance().sendMessage(cp, "You're muted!");
            return;
        }
        ChatChannel cc = cp.getChatChannel();
        if (!cc.isAvailable(cp)) {
            cc = ChatChannel.getDefaultChannel();
            cp.setChatChannel(cc);
        }
        if (cp.getOptions().isChannelDisabled(cc.getName())) {
            Core.getInstance().sendMessage(cp, "You have " + cc.getName() + " muted!");
            Core.getInstance().sendMessage(cp, "To unmute, go to Menu->Options->Chat Channels");
            return;
        }
        msg = cc.formatMessage(cp, msg, url);
        if (cp.isMuted() == 2) {
            cp.sendMessage(msg);
        } else {
            Core.getInstance().sendPacket(new PacketChatSpigot(cp.getUniqueId(), cc.getChannel().name(), msg));
        }
    }

    public static void sendMessage(ChatChannel channel, String msg) {
        Core.getInstance().sendPacket(new PacketChatSpigot(null, channel.getChannel().name(), msg));
    }

    public static void sendMessage(ChatChannel channel, String msg, Set<UUID> blacklist) {
        Core.getInstance().sendPacket(new PacketChatSpigot(null, channel.getChannel().name(), msg, blacklist));
    }

    public static void sendMessageHere(ChatChannel channel, String msg) {
        sendMessage(new PacketChatBungee(null, channel.getChannel().name(), msg));
    }

    public static void sendMessage(PacketChatBungee packet) {
        UUID uuid = packet.sender;
        CorePlayer sender = uuid != null ? Core.getInstance().getPlayers().get(uuid) : null;
        ChatChannel chatChannel = ChatChannel.getChannel(ChatChannel.Channel.valueOf(packet.channel));
        String message = packet.message;

        if (sender != null) {
            for (CorePlayer cp : chatChannel.getPlayers(sender)) {
                if (cp.getOnlineState() == DBPlayer.OnlineState.HERE &&
                        chatChannel.isAvailable(cp) &&
                        !cp.getOptions().isChannelDisabled(chatChannel.getName()) &&
                        !packet.blacklist.contains(cp.getUniqueId())) {
                    cp.sendMessage(message);
                }
            }
        } else {
            for (CorePlayer cp : Core.getInstance().getPlayers().getAllHere()) {
                if (cp.getOnlineState() == DBPlayer.OnlineState.HERE &&
                        chatChannel.isAvailable(cp) &&
                        !cp.getOptions().isChannelDisabled(chatChannel.getName()) &&
                        !packet.blacklist.contains(cp.getUniqueId())) {
                    cp.sendMessage(message);
                }
            }
        }
    }

    public static void sendTitle(ChatChannel channel, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (CorePlayer cp : Core.getInstance().getPlayers().getAllHere()) {
            if (!cp.getOptions().isChannelDisabled(channel.getName())
                    && channel.isAvailable(cp)) {
                cp.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }
        }
    }
    public static void sendTitle(CorePlayer cp, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (cp != null && cp.getPlayer() != null)
            cp.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendMessageToPlayer(CorePlayer cp, String msg) {
        if (cp != null && cp.getPlayer() != null)
            cp.getPlayer().sendMessage(chatColors.get("DEFAULT") + colorize(msg));
    }

    public static void sendMessageToPlayerSuccess(CorePlayer cp, String msg) {
        if (cp != null && cp.getPlayer() != null)
            cp.getPlayer().sendMessage(chatColors.get("SUCCESS") + colorize(msg));
    }

    public static void sendMessageToPlayerError(CorePlayer cp, String msg) {
        if (cp != null && cp.getPlayer() != null)
            cp.getPlayer().sendMessage(chatColors.get("ERROR") + colorize(msg));
    }

    public static void sendMessageToPlayerInvalid(CorePlayer cp, String msg) {
        if (cp != null && cp.getPlayer() != null)
            cp.getPlayer().sendMessage(chatColors.get("ERROR") + "Invalid command: " + colorize(msg));
    }

    public static void sendMessageToPlayerInfo(CorePlayer cp, String msg) {
        if (cp != null && cp.getPlayer() != null)
            cp.getPlayer().sendMessage(chatColors.get("INFO") + colorize(msg));
    }
    
    public static void sendRequest(String message, CorePlayer receiver, CorePlayer sender, BiConsumer<CorePlayer, CorePlayer> action) {
        RequestManager.sendRequest(Core.getInstance().getChatPrefix(), message, receiver, sender.getName(), new PlayerRequest(action));
    }
    
    public static void sendRequest(String message, CorePlayer receiver, String requestType, BiConsumer<CorePlayer, String> action) {
        RequestManager.sendRequest(Core.getInstance().getChatPrefix(), message, receiver, requestType, new ConsoleRequest(action));
    }

    /**
     * Send a message from one player to another
     *
     * @param sender CorePlayer
     * @param target CorePlayer
     * @param msg Message
     */
    public static void sendTell(CorePlayer sender, CorePlayer target, String msg) {
        sender.sendMessage(Chat.DEFAULT + "[me -> " + target.getDisplayName() + "] " + Chat.WHISPER + msg);
        Core.getInstance().sendPacket(new PacketTellSpigot(sender.getUniqueId(), target.getUniqueId(), msg));
    }

    public static void receiveTell(CorePlayer sender, CorePlayer target, String msg) {
        target.sendMessage(Chat.DEFAULT + "[" + sender.getDisplayName() + " -> me] " + Chat.WHISPER + msg);
        target.setReply(sender.getPlayer());
        target.getPlayer().playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }

    /**
     * Send a title to all players, stay is based on how long message is
     * Used by /broadcast command
     *
     * @param msg Message
     */
    public static void broadcast(String msg) {
        String title, subtitle;
        String[] msgs = msg.split("\\n");
        title = msgs[0];
        subtitle = msgs.length > 1 ? msgs[1] : "";
        Chat.sendTitle(ChatChannel.getDefaultChannel(), Chat.BROADCAST + title, Chat.BROADCAST + subtitle, 5, msg.length() * 2 + 10, 15);
    }

}
