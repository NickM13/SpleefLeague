/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.ChatColor;

/**
 * @author NickM13
 */
public class ChatChannel {
    
    public enum Channel {
        GLOBAL,
        PARTY,
        LOCAL,
        VIP,
        BUILD,
        STAFF,
        ADMIN,
        GAMES,
        TICKET,
        SPLEEF,
        SUPERJUMP;
        
        public ChatChannel getChatChannel() {
            return ChatChannel.getChannel(this);
        }
    }
    
    private static ChatChannel DEFAULT_CHANNEL;
    private static HashMap<Channel, ChatChannel> channels = new HashMap<>();

    private final Channel channel;
    private final String name;
    private final ChatColor color;
    private final String messageColor;
    private final Function<CorePlayer, Boolean> availableFun;
    private final Function<CorePlayer, Set<CorePlayer>> playerListFun;
    
    private enum PlayerNameFormat {
        NONE,
        BRACKET,
        COLON
    }
    private final PlayerNameFormat playerNameFormat;
    
    public ChatChannel(Channel channel, String name) {
        this.channel = channel;
        this.name = name;
        this.color = null;
        this.availableFun = null;
        this.playerNameFormat = PlayerNameFormat.BRACKET;
        this.playerListFun = null;
        this.messageColor = Chat.PLAYER_CHAT;
    }
    
    public ChatChannel(Channel channel, String name, ChatColor color, Function<CorePlayer, Boolean> availableFun, String messageColor) {
        this.channel = channel;
        this.name = name;
        this.color = color;
        this.availableFun = availableFun;
        this.playerNameFormat = PlayerNameFormat.COLON;
        this.playerListFun = null;
        this.messageColor = messageColor;
    }
    
    public ChatChannel(Channel channel, String name, ChatColor color, Function<CorePlayer, Boolean> availableFun,
                       Function<CorePlayer, Set<CorePlayer>> playerListFun, String messageColor) {
        this.channel = channel;
        this.name = name;
        this.color = color;
        this.availableFun = availableFun;
        this.playerNameFormat = PlayerNameFormat.COLON;
        this.playerListFun = playerListFun;
        this.messageColor = messageColor;
    }
    
    public String formatMessage(CorePlayer sender, String msg, boolean url) {
        String message = "";
        switch (playerNameFormat) {
            case BRACKET:
                message += Chat.BRACKET + "<";
                if (sender.getRank().getDisplayNameUnformatted().length() > 0) {
                    message += Chat.TAG_BRACE + "[" + sender.getRank().getDisplayName() + Chat.TAG_BRACE + "] ";
                }
                message += sender.getDisplayName() + Chat.BRACKET + "> ";
                break;
            case COLON:
                message += Chat.TAG_BRACE + "[" + color + name + Chat.TAG_BRACE + "] ";
                message += sender.getDisplayName() + Chat.BRACKET + ": ";
                break;
            default: break;
        }
        message += Chat.PLAYER_CHAT;
        if (url) message += msg;
        else message += Chat.colorize(msg);
        return message;
    }
    
    public String formatMessage(String msg) {
        String message = "";
        message += "[" + name + "] ";
        message += Chat.colorize(msg);
        return message;
    }

    public Channel getChannel() {
        return channel;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isAvailable(CorePlayer cp) {
        if (availableFun == null) return true;
        return availableFun.apply(cp);
    }
    
    public Set<CorePlayer> getPlayers(CorePlayer cp) {
        if (playerListFun == null) return Sets.newHashSet(Core.getInstance().getPlayers().getOnline());
        return playerListFun.apply(cp);
    }

    public static ChatChannel createTempChannel(String name) {
        return new ChatChannel(Channel.GLOBAL, name);
    }
    
    public static ChatChannel getChannel(Channel name) {
        return channels.get(name);
    }
    
    public static ChatChannel getDefaultChannel() {
        return getChannel(Channel.GLOBAL);
    }
    
    public static void addChatChannel(Channel id, String name, ChatColor color,
                                      Function<CorePlayer, Boolean> availableFun, String messageColor) {
        channels.put(id, new ChatChannel(id, name, color, availableFun, messageColor));
    }
    public static void addChatChannel(Channel id, String name, ChatColor color,
                                      Function<CorePlayer, Boolean> availableFun,
                                      Function<CorePlayer, Set<CorePlayer>> playerListFun,
                                      String messageColor) {
        channels.put(id, new ChatChannel(id, name, color, availableFun, playerListFun, messageColor));
    }

    public static void init() {
        channels.put(Channel.GLOBAL, new ChatChannel(Channel.GLOBAL, "Global"));
        addChatChannel(Channel.PARTY, "Party", ChatColor.AQUA, cp -> cp.getParty() != null, cp -> cp.getParty().getPlayers(), Chat.PLAYER_CHAT);
        addChatChannel(Channel.LOCAL, "Local", ChatColor.GRAY, cp -> cp.getGlobalZone() != null, cp -> cp.getGlobalZone().getPlayers(), Chat.PLAYER_CHAT);
        addChatChannel(Channel.VIP, "VIP", ChatColor.DARK_PURPLE, cp -> cp.getRank().hasPermission(Rank.getRank("VIP")), Chat.PLAYER_CHAT);
        addChatChannel(Channel.BUILD, "Build", ChatColor.GREEN, cp -> cp.getRank().hasPermission(Rank.getRank("BUILDER")), Chat.PLAYER_CHAT);
        addChatChannel(Channel.STAFF, "Staff", ChatColor.LIGHT_PURPLE, cp -> cp.getRank().hasPermission(Rank.getRank("MODERATOR")), Chat.PLAYER_CHAT);
        addChatChannel(Channel.ADMIN, "Admin", ChatColor.RED, cp -> cp.getRank().hasPermission(Rank.getRank("DEVELOPER")), Chat.PLAYER_CHAT);
        addChatChannel(Channel.GAMES, "Games", ChatColor.AQUA, null, Chat.DEFAULT);
        addChatChannel(Channel.TICKET, "Ticket", ChatColor.GOLD, cp -> cp.getRank().hasPermission(Rank.getRank("MODERATOR")), Chat.PLAYER_CHAT);
        addChatChannel(Channel.SPLEEF, "Spleef", ChatColor.GOLD, null, Chat.DEFAULT);
        addChatChannel(Channel.SUPERJUMP, "SuperJump", ChatColor.GOLD, null, Chat.DEFAULT);
    }
    
}