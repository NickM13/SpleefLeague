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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.spleefleague.core.request.RequestManager;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.chat.ChatEmoticons;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatConsole;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatFriend;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatPlayer;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatTell;
import net.md_5.bungee.api.chat.*;

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
            GAMEMAP = ChatColor.GREEN + "",
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

    private static final Pattern URL_PATTERN = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    public static Pattern getUrlPattern() {
        return URL_PATTERN;
    }

    public static ChatColor getColor(String color) {
        return chatColors.get(color);
    }

    public static void init() {
        chatColors.put("DEFAULT", ChatColor.GRAY);
        chatColors.put("SUCCESS", ChatColor.GREEN);
        chatColors.put("INFO", ChatColor.YELLOW);
        chatColors.put("ERROR", ChatColor.RED);
    }

    private static class FormattedPlayerMessage {

        TextComponent textComponent;
        boolean containsUrl;

        public FormattedPlayerMessage(TextComponent textComponent, boolean containsUrl) {
            this.textComponent = textComponent;
            this.containsUrl = containsUrl;
        }

    }

    private static FormattedPlayerMessage formatPlayerMessage(String message, TextComponent baseFormat) {
        TextComponent textComponent = (TextComponent) baseFormat.duplicate();

        Matcher urlMatcher = URL_PATTERN.matcher(message);
        StringBuilder builder = new StringBuilder();
        TextComponent component;
        boolean url = false;
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == ':') {
                int pos = message.indexOf(':', i + 1);
                if (pos != -1) {
                    String str = message.substring(i + 1, pos);
                    String emote = ChatEmoticons.getEmoticons().get(str);
                    if (emote != null) {
                        if (builder.length() > 0) {
                            component = new TextComponent(baseFormat);
                            component.setText(builder.toString());
                            textComponent.addExtra(component);
                            builder = new StringBuilder();
                        }

                        component = new TextComponent(emote);
                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(":" + str + ":").create()));
                        component.setColor(net.md_5.bungee.api.ChatColor.RESET);
                        textComponent.addExtra(component);
                        i = pos;
                        continue;
                    }
                }
            } else {
                int pos = message.indexOf(' ', i);
                if (pos == -1) {
                    pos = message.length();
                }
                if (urlMatcher.region(i, pos).find()) {
                    url = true;

                    if (builder.length() > 0) {
                        component = new TextComponent(baseFormat);
                        component.setText(builder.toString());
                        textComponent.addExtra(component);
                        builder = new StringBuilder();
                    }

                    String urlString = message.substring(i, pos);
                    component = new TextComponent(baseFormat);
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
            component = new TextComponent(baseFormat);
            component.setText(builder.toString());
            textComponent.addExtra(component);
        }

        return new FormattedPlayerMessage(textComponent, url);
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
        Stack<com.spleefleague.coreapi.chat.ChatColor> colorStack = new Stack<>();
        for (i = 0; i < msg.length() - 1; i++) {
            if (msg.charAt(i) == '&' || msg.charAt(i) == '§') {
                if (i >= msg.length() - 1) continue;
                switch (msg.charAt(i + 1)) {
                    case 'b':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.AQUA));
                        break;
                    case '0':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.BLACK));
                        break;
                    case '9':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.BLUE));
                        break;
                    case '3':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.DARK_AQUA));
                        break;
                    case '1':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.DARK_BLUE));
                        break;
                    case '8':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.DARK_GRAY));
                        break;
                    case '2':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.DARK_GREEN));
                        break;
                    case '5':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.DARK_PURPLE));
                        break;
                    case '4':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.DARK_RED));
                        break;
                    case '6':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.GOLD));
                        break;
                    case '7':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.GRAY));
                        break;
                    case 'a':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.GREEN));
                        break;
                    case 'd':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.LIGHT_PURPLE));
                        break;
                    case 'c':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.RED));
                        break;
                    case 'f':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.WHITE));
                        break;
                    case 'e':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.YELLOW));
                        break;
                    case 'l':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.BOLD));
                        break;
                    case 'i':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.ITALIC));
                        break;
                    case 'r':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.RESET));
                        break;
                    case 'n':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.UNDERLINE));
                        break;
                    case 'm':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.STRIKETHROUGH));
                        break;
                    case 'k':
                        newmsg.append(colorStack.push(com.spleefleague.coreapi.chat.ChatColor.MAGIC));
                        break;
                    case 'u':
                        if (colorStack.size() <= 1) {
                            newmsg.append(com.spleefleague.coreapi.chat.ChatColor.RESET);
                        } else {
                            colorStack.pop();
                            newmsg.append(colorStack.peek());
                        }
                        break;
                    default:
                        newmsg = new StringBuilder(newmsg.toString().concat(Character.toString(msg.charAt(i))).concat(Character.toString(msg.charAt(i + 1))));
                        break;
                }
                i++;
            } else if (msg.charAt(i) == '\\') {
                if (msg.charAt(i + 1) == 'n') {
                    newmsg.append("\n");
                } else if (msg.charAt(i + 1) == '\\') {
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

    public static void sendFakeMessage(CorePlayer sender, ChatChannel channel, String message) {
        if (channel == null) channel = sender.getChatChannel();

        if (!channel.isAvailable(sender)) {
            Core.getInstance().sendMessage(sender, "You have " + channel.getName() + " muted!");
            Core.getInstance().sendMessage(sender, "To unmute, go to Menu->Options->Chat Channels");
            return;
        }

        FormattedPlayerMessage playerMessage = formatPlayerMessage(message, channel.getPlayerMessageBase());

        if (playerMessage.containsUrl) {
            if (!sender.canSendUrl()) {
                Core.getInstance().sendMessage(sender, "Please ask for permission to send a URL");
                return;
            } else {
                //sender.disallowUrl();
            }
        }

        TextComponent textComponent = new TextComponent();

        textComponent.addExtra(channel.getTagComponent());
        textComponent.addExtra(channel.isShowingTag() ? sender.getChatName() : sender.getChatNameRanked());
        textComponent.addExtra(net.md_5.bungee.api.ChatColor.GRAY + ": ");
        textComponent.addExtra(playerMessage.textComponent);

        sender.sendMessage(textComponent);
    }

    public static void sendMessage(CorePlayer sender, String message) {
        sendMessage(sender, sender.getChatChannel(), message);
    }

    public static void sendMessage(CorePlayer sender, ChatChannel channel, String message) {
        if (channel == null) channel = sender.getChatChannel();

        if (!channel.isAvailable(sender)) {
            Core.getInstance().sendMessage(sender, "You have " + channel.getName() + " muted!");
            Core.getInstance().sendMessage(sender, "To unmute, go to Menu->Options->Chat Channels");
            return;
        }

        FormattedPlayerMessage playerMessage = formatPlayerMessage(message, channel.getPlayerMessageBase());

        if (playerMessage.containsUrl) {
            if (!sender.canSendUrl()) {
                Core.getInstance().sendMessage(sender, "Please ask for permission to send a URL");
                return;
            } else {
                //sender.disallowUrl();
            }
        }

        if (channel.isGlobal()) {
            Core.getInstance().sendPacket(new PacketSpigotChatPlayer(sender.getUniqueId(), channel.name(), message));
        } else {
            TextComponent textComponent = new TextComponent();

            textComponent.addExtra(channel.getTagComponent());
            textComponent.addExtra(channel.isShowingTag() ? sender.getChatName() : sender.getChatNameRanked());
            textComponent.addExtra(net.md_5.bungee.api.ChatColor.GRAY + ": ");
            textComponent.addExtra(playerMessage.textComponent);

            for (CorePlayer cp : Core.getInstance().getPlayers().getAllLocal()) {
                if (channel.isActive(cp)) {
                    cp.sendMessage(textComponent);
                }
            }
        }
    }

    private static final String LINEBREAK = ChatColor.GOLD + "" + ChatColor.BOLD + "- - - - - - - - - - - - - - - - - - - - - - - - - - -";

    public static void sendNpcMessage(CorePlayer receiver, String profile, String name, String message) {
        ComponentBuilder builder = new ComponentBuilder()
                .append(LINEBREAK, ComponentBuilder.FormatRetention.NONE)
                .append("\n" + profile, ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.WHITE).italic(false)
                .append(" " + name.replaceAll("_", " "), ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GOLD).bold(true);
        int i = 0;
        for (String str : message.split("\\\\n")) {
            builder.append("\n亖 " + str, ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).italic(true);
            i++;
        }
        for (; i <= 4; i++) {
            builder.append("\n");
        }
        builder.append(LINEBREAK, ComponentBuilder.FormatRetention.NONE);
        receiver.sendMessage(builder.create());
    }

    public static void sendNpcMessage(String profile, String name, String message) {
        ComponentBuilder builder = new ComponentBuilder()
                .append(LINEBREAK, ComponentBuilder.FormatRetention.NONE)
                .append("\n" + profile, ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.WHITE).italic(false)
                .append(" " + name.replaceAll("_", " "), ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GOLD).bold(true);
        int i = 0;
        for (String str : message.split("\\\\n")) {
            builder.append("\n亖 " + str, ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).italic(true);
            i++;
        }
        for (; i <= 4; i++) {
            builder.append("\n");
        }
        builder.append(LINEBREAK, ComponentBuilder.FormatRetention.NONE);
        Chat.sendMessageLocal(builder.create());
    }

    private static void sendMessageLocal(BaseComponent... baseComponents) {
        for (CorePlayer cp : Core.getInstance().getPlayers().getAllLocal()) {
            cp.sendMessage(baseComponents);
        }
    }

    public static void sendNpcMessage(CorePlayer receiver, NpcMessage message) {
        ComponentBuilder builder = new ComponentBuilder()
                .append(LINEBREAK, ComponentBuilder.FormatRetention.NONE)
                .append("\n" + message.getProfile(), ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.WHITE).italic(false)
                .append(" " + message.getName(), ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GOLD).bold(true);
        int i = 0;
        for (String str : message.getMessages()) {
            builder.append("\n亖 " + str, ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.GREEN).italic(true);
            i++;
        }
        for (; i <= 4; i++) {
            builder.append("\n");
        }
        builder.append(LINEBREAK, ComponentBuilder.FormatRetention.NONE);
        receiver.sendMessage(builder.create());
    }

    ///npc 倗 Barmaid_Melissa What'll it be honey?\nOur Tree Stump Ales are made from the Valley's own\ntrees. You won't find a better Ale anywhere!

    public static void sendMessage(ChatChannel channel, TextComponent text) {
        Core.getInstance().sendPacket(new PacketSpigotChatConsole(channel.name(), text.toLegacyText(), false));
    }

    public static void sendMessage(ChatChannel channel, TextComponent text, Set<UUID> blacklist) {
        Core.getInstance().sendPacket(new PacketSpigotChatConsole(channel.name(), text.toLegacyText(), blacklist, false));
    }

    public static void sendMessageFriends(ChatChannel channel, TextComponent text, Set<UUID> targets) {
        Core.getInstance().sendPacket(new PacketSpigotChatFriend(channel.name(), text.toLegacyText(), targets));
    }

    public static void sendTitle(ChatChannel channel, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (CorePlayer cp : Core.getInstance().getPlayers().getAllLocal()) {
            if (cp.getOptions().getBoolean("Chat:" + channel.getName())
                    && channel.isAvailable(cp)) {
                cp.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }
        }
    }

    public static void sendTitle(CorePlayer cp, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (cp != null && cp.getPlayer() != null)
            cp.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendMessageToPlayer(CorePlayer cp, String message) {
        cp.sendMessage(Chat.DEFAULT + message);
    }

    public static void sendMessageToPlayer(CorePlayer cp, TextComponent text) {
        if (cp != null && cp.getPlayer() != null) {
            TextComponent recolored = new TextComponent(text);
            recolored.setColor(net.md_5.bungee.api.ChatColor.GRAY);
            cp.sendMessage(recolored);
        }
    }

    public static void sendMessageToPlayerSuccess(CorePlayer cp, TextComponent text) {
        if (cp != null && cp.getPlayer() != null) {
            TextComponent recolored = new TextComponent(text);
            recolored.setColor(net.md_5.bungee.api.ChatColor.GREEN);
            cp.sendMessage(recolored);
        }
    }

    public static void sendMessageToPlayerError(CorePlayer cp, TextComponent text) {
        if (cp != null && cp.getPlayer() != null) {
            TextComponent recolored = new TextComponent(text);
            recolored.setColor(net.md_5.bungee.api.ChatColor.RED);
            cp.sendMessage(recolored);
        }
    }

    public static void sendMessageToPlayerInfo(CorePlayer cp, TextComponent text) {
        if (cp != null && cp.getPlayer() != null) {
            TextComponent recolored = new TextComponent(text);
            recolored.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
            cp.sendMessage(recolored);
        }
    }

    public static void sendRequest(CorePlayer receiver, CorePlayer sender, BiConsumer<CorePlayer, CorePlayer> action, String message) {
        RequestManager.sendPlayerRequest(Core.getInstance().getChatPrefix(), receiver, sender, action, new TextComponent(message));
    }

    public static void sendRequest(CorePlayer receiver, String requestType, BiConsumer<CorePlayer, String> action, String message) {
        RequestManager.sendConsoleRequest(Core.getInstance().getChatPrefix(), receiver, requestType, action, new TextComponent(message));
    }

    public static void sendRequest(CorePlayer receiver, CorePlayer sender, BiConsumer<CorePlayer, CorePlayer> action, BaseComponent... messages) {
        RequestManager.sendPlayerRequest(Core.getInstance().getChatPrefix(), receiver, sender, action, messages);
    }

    public static void sendRequest(CorePlayer receiver, String requestType, BiConsumer<CorePlayer, String> action, BaseComponent... messages) {
        RequestManager.sendConsoleRequest(Core.getInstance().getChatPrefix(), receiver, requestType, action, messages);
    }

    public static void sendConfirmationButtons(CorePlayer receiver, String acceptCmd, String declineCmd) {
        TextComponent text = new TextComponent();

        TextComponent accept = new TextComponent(Chat.TAG_BRACE + "[" + Chat.SUCCESS + "Accept" + Chat.TAG_BRACE + "]");
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept").create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, acceptCmd));
        TextComponent decline = new TextComponent(Chat.TAG_BRACE + "[" + Chat.ERROR + "Decline" + Chat.TAG_BRACE + "]");
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to decline").create()));
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, declineCmd));

        text.addExtra(accept);
        text.addExtra(" ");
        text.addExtra(decline);

        Core.getInstance().sendMessage(receiver, text);
    }

    /**
     * Send a message from one player to another
     *
     * @param sender CorePlayer
     * @param target CorePlayer
     * @param msg    Message
     */
    public static void sendTell(CorePlayer sender, CorePlayer target, String msg) {
        Core.getInstance().sendPacket(new PacketSpigotChatTell(sender.getUniqueId(), target.getUniqueId(), msg));
    }

}
