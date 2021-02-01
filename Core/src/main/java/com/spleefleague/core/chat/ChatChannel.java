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
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.player.rank.CoreRankManager;
import com.spleefleague.core.util.CoreUtils;
import net.md_5.bungee.api.chat.ClickEvent;
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

    private final boolean showTag;
    private final Channel channel;
    private final String name;
    private final ChatColor color;
    private final String messageColor;
    private TextComponent formattedComponent;
    private final Function<CorePlayer, Boolean> availableFun;
    private final Function<CorePlayer, Set<CorePlayer>> playerListFun;
    
    private enum PlayerNameFormat {
        NONE,
        COLON
    }
    private final PlayerNameFormat playerNameFormat;
    
    public ChatChannel(Channel channel, String name) {
        this.channel = channel;
        this.name = name;
        this.color = ChatColor.GRAY;
        this.availableFun = null;
        this.playerNameFormat = PlayerNameFormat.COLON;
        this.playerListFun = null;
        this.messageColor = Chat.PLAYER_CHAT;
        this.showTag = false;

        updateFormattedComponent();
    }
    
    public ChatChannel(Channel channel, String name, ChatColor color, Function<CorePlayer, Boolean> availableFun, String messageColor) {
        this.channel = channel;
        this.name = name;
        this.color = color;
        this.availableFun = availableFun;
        this.playerNameFormat = PlayerNameFormat.COLON;
        this.playerListFun = null;
        this.messageColor = messageColor;
        this.showTag = true;

        updateFormattedComponent();
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
        this.showTag = true;

        updateFormattedComponent();
    }

    private void updateFormattedComponent() {
        formattedComponent = new TextComponent();
        for (net.md_5.bungee.api.ChatColor chatColor : CoreUtils.getChatColors(messageColor)) {
            switch (chatColor) {
                case RESET: break;
                case STRIKETHROUGH: formattedComponent.setStrikethrough(true); break;
                case BOLD: formattedComponent.setBold(true); break;
                case UNDERLINE: formattedComponent.setUnderlined(true); break;
                case MAGIC: formattedComponent.setObfuscated(true); break;
                case ITALIC: formattedComponent.setItalic(true); break;
                default: formattedComponent.setColor(chatColor);
            }
        }
    }
    
    public TextComponent formatMessage(CorePlayer sender, String msg, boolean url) {
        TextComponent textComponent = new TextComponent(formattedComponent);
        if (sender != null) {
            switch (playerNameFormat) {
                case COLON:
                    if (showTag) {
                        textComponent.addExtra(getChatTag());
                        textComponent.addExtra(sender.getChatName());
                    } else {
                        textComponent.addExtra(sender.getChatNameRanked());
                    }
                    textComponent.addExtra(Chat.BRACKET + ": ");
                    break;
                default:
                    break;
            }
        }
        Matcher urlMatcher = Chat.getUrlPattern().matcher(msg);
        StringBuilder builder = new StringBuilder();
        TextComponent component;
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            if (c == ':') {
                int pos = msg.indexOf(':', i + 1);
                if (pos != -1) {
                    String str = msg.substring(i + 1, pos);
                    String emote = ChatEmoticons.getEmoticons().get(str);
                    if (emote != null) {
                        if (builder.length() > 0) {
                            component = new TextComponent(formattedComponent);
                            component.setText(builder.toString());
                            textComponent.addExtra(component);
                            builder = new StringBuilder();
                        }

                        component = new TextComponent(emote);
                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(":" + str + ":").create()));
                        component.setColor(net.md_5.bungee.api.ChatColor.RESET);
                        textComponent.addExtra(component);
                        i = pos - 1;
                        continue;
                    }
                }
            } else {
                int pos = msg.indexOf(' ', i);
                if (pos == -1) {
                    pos = msg.length();
                }
                if (urlMatcher.region(i, pos).find()) {
                    if (builder.length() > 0) {
                        component = new TextComponent(formattedComponent);
                        component.setText(builder.toString());
                        textComponent.addExtra(component);
                        builder = new StringBuilder();
                    }

                    String urlString = msg.substring(i, pos);
                    component = new TextComponent(formattedComponent);
                    component.setText(urlString);
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlString.startsWith("http") ? urlString : "http://" + urlString));
                    textComponent.addExtra(component);
                    i = pos - 1;
                    continue;
                }
            }
            builder.append(c);
        }
        if (builder.length() > 0) {
            component = new TextComponent(formattedComponent);
            component.setText(builder.toString());
            textComponent.addExtra(component);
        }
        /*
        if (!url) {
            StringBuilder piece = new StringBuilder();
            boolean regular = true;
            for (char c : msg.toCharArray()) {
                if (c == ':') {
                    if (regular) {
                        textComponent.addExtra(messageColor + piece.toString());
                        piece = new StringBuilder(":");
                    } else {
                        piece.append(":");
                        if (ChatEmoticons.getEmoticons().containsKey(piece.toString())) {
                            textComponent.addExtra(new ComponentBuilder(ChatEmoticons.getEmoticons().get(piece.toString())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(piece.toString()).create())).create()[0]);
                        } else {
                            textComponent.addExtra(messageColor + piece.toString());
                        }
                        piece = new StringBuilder();
                    }
                    regular = !regular;
                } else {
                    piece.append(c);
                }
            }
            if (piece.length() > 0) {
                textComponent.addExtra(messageColor + piece.toString());
            }
        } else {
            textComponent.addExtra(msg);
        }
        */
        return textComponent;
    }

    public Channel getChannel() {
        return channel;
    }
    
    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return color + name;
    }

    public String getChatTag() {
        return Chat.TAG_BRACE + "[" + color + name + Chat.TAG_BRACE + "] ";
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

    public static Set<String> getChannelNames() {
        return channels.keySet().stream().map(Enum::name).collect(Collectors.toSet());
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
        registerChatChannel(Channel.LOGIN, "Friends", ChatColor.GRAY, null, cp -> cp.getFriends().getOnline(), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.PARTY, "Party", ChatColor.AQUA, cp -> cp.getParty() != null, cp -> cp.getParty().getLocalPlayers(), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.LOCAL, "Local", ChatColor.GRAY, cp -> cp.getGlobalZone() != null, cp -> cp.getGlobalZone().getPlayers(), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.VIP, "VIP", ChatColor.DARK_PURPLE, cp -> cp.getRank().hasPermission(CoreRank.VIP), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.BUILD, "Build", ChatColor.GREEN, cp -> cp.getRank().hasPermission(CoreRank.BUILDER), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.STAFF, "Staff", ChatColor.LIGHT_PURPLE, cp -> cp.getRank().hasPermission(CoreRank.MODERATOR), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.ADMIN, "Admin", ChatColor.RED, cp -> cp.getRank().hasPermission(CoreRank.DEVELOPER), ChatColor.GREEN + "");
        registerChatChannel(Channel.GAMES, "Games", ChatColor.AQUA, null, Chat.DEFAULT);
        registerChatChannel(Channel.TICKET, "Ticket", ChatColor.GOLD, cp -> cp.getRank().hasPermission(CoreRank.MODERATOR), Chat.PLAYER_CHAT);
        registerChatChannel(Channel.SPLEEF, "Spleef", ChatColor.GOLD, null, Chat.DEFAULT);
        registerChatChannel(Channel.SUPERJUMP, "SuperJump", ChatColor.GOLD, null, Chat.DEFAULT);
    }
    
}
