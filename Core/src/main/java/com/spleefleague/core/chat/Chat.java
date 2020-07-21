/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.chat;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.spleefleague.core.request.ConsoleRequest;
import com.spleefleague.core.request.PlayerRequest;
import com.spleefleague.core.request.RequestManager;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author NickM13
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
        for (i = 0; i < msg.length() - 1; i++) {
            if (msg.charAt(i) == '&') {
                switch (msg.charAt(i+1)) {
                    case 'b': newmsg.append(ChatColor.AQUA); break;
                    case '0': newmsg.append(ChatColor.BLACK); break;
                    case '9': newmsg.append(ChatColor.BLUE); break;
                    case '3': newmsg.append(ChatColor.DARK_AQUA); break;
                    case '1': newmsg.append(ChatColor.DARK_BLUE); break;
                    case '8': newmsg.append(ChatColor.DARK_GRAY); break;
                    case '2': newmsg.append(ChatColor.DARK_GREEN); break;
                    case '5': newmsg.append(ChatColor.DARK_PURPLE); break;
                    case '4': newmsg.append(ChatColor.DARK_RED); break;
                    case '6': newmsg.append(ChatColor.GOLD); break;
                    case '7': newmsg.append(ChatColor.GRAY); break;
                    case 'a': newmsg.append(ChatColor.GREEN); break;
                    case 'd': newmsg.append(ChatColor.LIGHT_PURPLE); break;
                    case 'c': newmsg.append(ChatColor.RED); break;
                    case 'f': newmsg.append(ChatColor.WHITE); break;
                    case 'e': newmsg.append(ChatColor.YELLOW); break;
                    case 'l': newmsg.append(ChatColor.BOLD); break;
                    case 'i': newmsg.append(ChatColor.ITALIC); break;
                    case 'r': newmsg.append(ChatColor.RESET); break;
                    case 'n': newmsg.append(ChatColor.UNDERLINE); break;
                    case 'm': newmsg.append(ChatColor.STRIKETHROUGH); break;
                    case 'k': newmsg.append(ChatColor.MAGIC); break;
                    default: newmsg = new StringBuilder(newmsg.toString().concat(Character.toString(msg.charAt(i))).concat(Character.toString(msg.charAt(i + 1)))); break;
                }
                i++;
            } else if (msg.charAt(i) == '\\') {
                if (msg.charAt(i+1) == 'n') {
                    newmsg.append("\n");
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
            ByteArrayDataOutput output = ByteStreams.newDataOutput();

            output.writeUTF(cp.getUniqueId().toString());
            output.writeUTF(cc.getChannel().name());
            output.writeUTF(msg);

            cp.getPlayer().sendPluginMessage(Core.getInstance(), "slcore:chat", output.toByteArray());
            //Core.getInstance().getServer().getMessenger().dispatchIncomingMessage(cp.getPlayer(), "core:chat", output.toByteArray());
        }
    }

    public static void sendMessage(ChatChannel channel, String msg) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeUTF("");
        output.writeUTF(channel.getChannel().name());
        output.writeUTF(msg);

        Objects.requireNonNull(Iterables.getFirst(Bukkit.getOnlinePlayers(), null)).sendPluginMessage(Core.getInstance(), "slcore:chat", output.toByteArray());
        /*
        for (CorePlayer cp : Core.getInstance().getPlayers().getOnline()) {
            if (!cp.getOptions().isChannelDisabled(channel.getName())
                    && channel.isAvailable(cp)) {
                cp.sendMessage(msg);
            }
        }
        */
    }

    public static void sendMessage(byte[] bytes) {
        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);

        String playerUuid = input.readUTF();
        CorePlayer cp = playerUuid.length() == 0 ? null : Core.getInstance().getPlayers().get(UUID.fromString(playerUuid));
        ChatChannel cc = ChatChannel.getChannel(ChatChannel.Channel.valueOf(input.readUTF()));
        String msg = input.readUTF();

        if (cp != null) {
            for (CorePlayer cp1 : cc.getPlayers(cp)) {
                if (cp1.getOnlineState() == DBPlayer.OnlineState.HERE
                        && cc.isAvailable(cp1)
                        && !cp1.getOptions().isChannelDisabled(cc.getName())) {
                    cp1.sendMessage(msg);
                }
            }
        } else {
            for (CorePlayer cp1 : Core.getInstance().getPlayers().getOnline()) {
                if (cp1.getOnlineState() == DBPlayer.OnlineState.HERE
                        && cc.isAvailable(cp1)
                        && !cp1.getOptions().isChannelDisabled(cc.getName())) {
                    cp1.sendMessage(msg);
                }
            }
        }
    }

    public static void sendTitle(ChatChannel channel, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (CorePlayer cp : Core.getInstance().getPlayers().getOnline()) {
            if (!cp.getOptions().isChannelDisabled(channel.getName())
                    && channel.isAvailable(cp)) {
                cp.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }
        }
    }
    public static void sendTitle(CorePlayer cp, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        cp.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendMessageToPlayer(CorePlayer cp, String msg) {
        if (cp != null && cp.getOnlineState() != DBPlayer.OnlineState.OFFLINE)
            cp.getPlayer().sendMessage(chatColors.get("DEFAULT") + colorize(msg));
    }

    public static void sendMessageToPlayerSuccess(CorePlayer cp, String msg) {
        if (cp != null && cp.getOnlineState() != DBPlayer.OnlineState.OFFLINE)
            cp.getPlayer().sendMessage(chatColors.get("SUCCESS") + colorize(msg));
    }

    public static void sendMessageToPlayerError(CorePlayer cp, String msg) {
        if (cp != null && cp.getOnlineState() != DBPlayer.OnlineState.OFFLINE)
            cp.getPlayer().sendMessage(chatColors.get("ERROR") + colorize(msg));
    }

    public static void sendMessageToPlayerInvalid(CorePlayer cp, String msg) {
        if (cp != null && cp.getOnlineState() != DBPlayer.OnlineState.OFFLINE)
            cp.getPlayer().sendMessage(chatColors.get("ERROR") + "Invalid command: " + colorize(msg));
    }

    public static void sendMessageToPlayerInfo(CorePlayer cp, String msg) {
        if (cp != null && cp.getOnlineState() != DBPlayer.OnlineState.OFFLINE)
            cp.getPlayer().sendMessage(chatColors.get("INFO") + colorize(msg));
    }
    
    public static void sendRequest(String message, CorePlayer receiver, CorePlayer sender, BiConsumer<CorePlayer, CorePlayer> action) {
        RequestManager.sendRequest(Core.getInstance().getChatPrefix(), message, receiver, sender.getName(), new PlayerRequest(action));
    }
    
    public static void sendRequest(String message, CorePlayer receiver, String requestType, BiConsumer<CorePlayer, String> action) {
        RequestManager.sendRequest(Core.getInstance().getChatPrefix(), message, receiver, requestType, new ConsoleRequest(action));
    }

}
