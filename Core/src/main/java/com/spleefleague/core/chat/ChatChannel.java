/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;

import java.util.*;
import java.util.function.Function;

import com.spleefleague.core.player.rank.Ranks;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
        LOGIN,
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
        this.playerNameFormat = PlayerNameFormat.COLON;
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
    
    public TextComponent formatMessage(CorePlayer sender, String msg, boolean url) {
        TextComponent textComponent = new TextComponent();
        if (sender != null) {
            switch (playerNameFormat) {
                case BRACKET:
                    /*
                    message += Chat.BRACKET + "<";
                    if (sender.getRank().getDisplayNameUnformatted().length() > 0) {
                        message += Chat.TAG_BRACE + "[" + sender.getRank().getDisplayName() + Chat.TAG_BRACE + "] ";
                    }
                    message += sender.getDisplayName() + Chat.BRACKET + "> ";
                    */
                    break;
                case COLON:
                    textComponent.addExtra(sender.getChatNameRanked());
                    textComponent.addExtra(Chat.BRACKET + ": ");
                    //componentBuilder.append(sender.getChatNameRanked()).append(": ");
                    //message += sender.getRank().getDisplayName();
                    //message += sender.getDisplayName() + Chat.BRACKET + ": ";
                    break;
                default:
                    break;
            }
        }
        if (!url) {
            StringBuilder piece = new StringBuilder();
            boolean regular = true;
            for (char c : msg.toCharArray()) {
                if (c == ':') {
                    if (regular) {
                        textComponent.addExtra(Chat.PLAYER_CHAT + piece.toString());
                        piece = new StringBuilder(":");
                    } else {
                        piece.append(":");
                        if (ChatEmoticons.getEmoticons().containsKey(piece.toString())) {
                            textComponent.addExtra(new ComponentBuilder(ChatEmoticons.getEmoticons().get(piece.toString())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(piece.toString()).create())).create()[0]);
                        } else {
                            textComponent.addExtra(Chat.PLAYER_CHAT + piece.toString());
                        }
                        piece = new StringBuilder();
                    }
                    regular = !regular;
                } else {
                    piece.append(c);
                }
            }
            if (piece.length() > 0) {
                textComponent.addExtra(Chat.PLAYER_CHAT + piece.toString());
            }
        } else {
            textComponent.addExtra(msg);
        }
        return textComponent;
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
        if (playerListFun == null) return Sets.newHashSet(Core.getInstance().getPlayers().getAllHere());
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

    public static void registerChatChannel(Channel id, String name) {
        channels.put(id, new ChatChannel(id, name));
    }

    public static void registerChatChannel(Channel id, String name, ChatColor color,
                                           Function<CorePlayer, Boolean> availableFun, String messageColor) {
        channels.put(id, new ChatChannel(id, name, color, availableFun, messageColor));
    }
    public static void registerChatChannel(Channel id, String name, ChatColor color,
                                           Function<CorePlayer, Boolean> availableFun,
                                           Function<CorePlayer, Set<CorePlayer>> playerListFun,
                                           String messageColor) {
        channels.put(id, new ChatChannel(id, name, color, availableFun, playerListFun, messageColor));
    }

    public static void init() {
        registerChatChannel(Channel.GLOBAL, "Global");
        registerChatChannel(Channel.LOGIN, "Login");
        registerChatChannel(Channel.PARTY, "Party", ChatColor.AQUA, cp -> cp.getParty() != null, cp -> cp.getParty().getPlayers(), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.LOCAL, "Local", ChatColor.GRAY, cp -> cp.getGlobalZone() != null, cp -> cp.getGlobalZone().getPlayers(), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.VIP, "VIP", ChatColor.DARK_PURPLE, cp -> cp.getRank().hasPermission(Ranks.getRank("VIP")), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.BUILD, "Build", ChatColor.GREEN, cp -> cp.getRank().hasPermission(Ranks.getRank("BUILDER")), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.STAFF, "Staff", ChatColor.LIGHT_PURPLE, cp -> cp.getRank().hasPermission(Ranks.getRank("MODERATOR")), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.ADMIN, "Admin", ChatColor.RED, cp -> cp.getRank().hasPermission(Ranks.getRank("DEVELOPER")), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.GAMES, "Games", ChatColor.AQUA, null, Chat.DEFAULT);
        registerChatChannel(Channel.TICKET, "Ticket", ChatColor.GOLD, cp -> cp.getRank().hasPermission(Ranks.getRank("MODERATOR")), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.SPLEEF, "Spleef", ChatColor.GOLD, null, Chat.DEFAULT);
        registerChatChannel(Channel.SUPERJUMP, "SuperJump", ChatColor.GOLD, null, Chat.DEFAULT);
    }
    
}
