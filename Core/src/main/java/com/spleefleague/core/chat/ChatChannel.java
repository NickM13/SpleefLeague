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
    
    private final String name;
    private final ChatColor color;
    private final Function<CorePlayer, Boolean> availableFun;
    private final Function<CorePlayer, Set<CorePlayer>> playerListFun;
    
    private enum PlayerNameFormat {
        NONE,
        BRACKET,
        COLON
    }
    private final PlayerNameFormat playerNameFormat;
    
    public ChatChannel(String name) {
        this.name = name;
        this.color = null;
        this.availableFun = null;
        this.playerNameFormat = PlayerNameFormat.BRACKET;
        this.playerListFun = null;
    }
    
    public ChatChannel(String name, ChatColor color, Function<CorePlayer, Boolean> availableFun) {
        this.name = name;
        this.color = color;
        this.availableFun = availableFun;
        this.playerNameFormat = PlayerNameFormat.COLON;
        this.playerListFun = null;
    }
    
    public ChatChannel(String name, ChatColor color, Function<CorePlayer, Boolean> availableFun, Function<CorePlayer, Set<CorePlayer>> playerListFun) {
        this.name = name;
        this.color = color;
        this.availableFun = availableFun;
        this.playerNameFormat = PlayerNameFormat.COLON;
        this.playerListFun = playerListFun;
    }
    
    public String formatMessage(CorePlayer sender, String msg) {
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
        message += Chat.PLAYER_CHAT + Chat.colorize(msg);
        return message;
    }
    
    public String formatMessage(String msg) {
        String message = "";
        message += "[" + name + "] ";
        message += Chat.colorize(msg);
        return message;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isAvailable(CorePlayer cp) {
        if (availableFun == null) return true;
        return availableFun.apply(cp);
    }
    
    public Set<CorePlayer> getPlayers(CorePlayer cp) {
        if (playerListFun == null) return Sets.newHashSet(Core.getInstance().getPlayers().getAll());
        return playerListFun.apply(cp);
    }

    public static ChatChannel createTempChannel(String name) {
        return new ChatChannel(name);
    }
    
    public static ChatChannel getChannel(Channel name) {
        // Create a tagless chat channel if one doesnt exist
        return channels.get(name);
    }
    
    public static ChatChannel getDefaultChannel() {
        return getChannel(Channel.GLOBAL);
    }
    
    public static void addChatChannel(Channel id, String name, ChatColor color, Function<CorePlayer, Boolean> availableFun) {
        channels.put(id, new ChatChannel(name, color, availableFun));
    }
    public static void addChatChannel(Channel id, String name, ChatColor color, Function<CorePlayer, Boolean> availableFun, Function<CorePlayer, Set<CorePlayer>> playerListFun) {
        channels.put(id, new ChatChannel(name, color, availableFun, playerListFun));
    }
    
    public static void init() {
        channels.put(Channel.GLOBAL, new ChatChannel("Global"));
        addChatChannel(Channel.PARTY, "Party", ChatColor.AQUA, cp -> cp.getParty() != null, cp -> cp.getParty().getPlayers());
        addChatChannel(Channel.VIP, "VIP", ChatColor.DARK_PURPLE, cp -> cp.getRank().hasPermission(Rank.getRank("VIP")));
        addChatChannel(Channel.BUILD, "Build", ChatColor.GREEN, cp -> cp.getRank().hasPermission(Rank.getRank("BUILDER")));
        addChatChannel(Channel.STAFF, "Staff", ChatColor.LIGHT_PURPLE, cp -> cp.getRank().hasPermission(Rank.getRank("MODERATOR")));
        addChatChannel(Channel.ADMIN, "Admin", ChatColor.RED, cp -> cp.getRank().hasPermission(Rank.getRank("DEVELOPER")));
        addChatChannel(Channel.GAMES, "Games", ChatColor.AQUA, null);
        addChatChannel(Channel.TICKET, "Ticket", ChatColor.GOLD, cp -> cp.getRank().hasPermission(Rank.getRank("MODERATOR")));
        addChatChannel(Channel.SPLEEF, "Spleef", ChatColor.GOLD, null);
        addChatChannel(Channel.SUPERJUMP, "SuperJump", ChatColor.GOLD, null);
    }
    
}
