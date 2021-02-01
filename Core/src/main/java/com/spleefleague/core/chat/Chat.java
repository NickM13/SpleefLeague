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
import java.util.regex.Pattern;

import com.spleefleague.core.request.RequestManager;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.bungee.chat.PacketBungeeChat;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChat;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatTell;
import net.md_5.bungee.api.chat.*;
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
            if (msg.charAt(i) == '&' || msg.charAt(i) == '§') {
                if (i >= msg.length() - 2) continue;
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
        sendMessage(cp.getChatChannel(), cp, msg, url);
    }

    public static void sendMessage(ChatChannel cc, CorePlayer cp, String message, boolean url) {
        if (cp.isMuted() == 1) {
            Core.getInstance().sendMessage(cp, "You're muted!");
            return;
        }
        if (!cc.isAvailable(cp)) {
            cc = ChatChannel.getDefaultChannel();
            cp.setChatChannel(cc);
        }
        if (!cp.getOptions().getBoolean(cc.getName())) {
            Core.getInstance().sendMessage(cp, "You have " + cc.getName() + " muted!");
            Core.getInstance().sendMessage(cp, "To unmute, go to Menu->Options->Chat Channels");
            return;
        }
        if (cp.isMuted() == 2) {
            cp.sendMessage(cc.formatMessage(cp, message, url));
        } else {
            Core.getInstance().sendPacket(new PacketSpigotChat(cp.getUniqueId(), cc.getChannel().name(), message, url));
        }
    }

    public static void sendMessage(ChatChannel channel, TextComponent text) {
        Core.getInstance().sendPacket(new PacketSpigotChat(null, channel.getChannel().name(), text.toLegacyText(), false));
    }

    public static void sendMessage(ChatChannel channel, TextComponent text, Set<UUID> blacklist) {
        Core.getInstance().sendPacket(new PacketSpigotChat(null, channel.getChannel().name(), text.toLegacyText(), blacklist, false));
    }

    public static void sendMessageHere(ChatChannel channel, TextComponent text) {
        sendMessage(new PacketBungeeChat(null, channel.getChannel().name(), text.toLegacyText(), false));
    }

    /**
     * When a chat packet is received, process it
     * PacketChatBungee::url is only used when sender is not null
     *
     * @param packet
     */
    public static void sendMessage(PacketBungeeChat packet) {
        UUID uuid = packet.sender;
        CorePlayer sender = uuid != null ? Core.getInstance().getPlayers().get(uuid) : null;
        ChatChannel chatChannel = ChatChannel.getChannel(ChatChannel.Channel.valueOf(packet.channel));
        TextComponent message;
        if (sender == null) {
            message = new TextComponent(TextComponent.fromLegacyText(packet.message));
        } else {
            message = chatChannel.formatMessage(sender, packet.message, packet.url);
        }

        if (sender != null) {
            for (CorePlayer cp : chatChannel.getPlayers(sender)) {
                if (cp.getOnlineState() == DBPlayer.OnlineState.HERE &&
                        chatChannel.isAvailable(cp) &&
                        cp.getOptions().getBoolean(chatChannel.getName()) &&
                        !packet.blacklist.contains(cp.getUniqueId())) {
                    cp.sendMessage(message);
                }
            }
        } else {
            for (CorePlayer cp : Core.getInstance().getPlayers().getAllHere()) {
                if (cp.getOnlineState() == DBPlayer.OnlineState.HERE &&
                        chatChannel.isAvailable(cp) &&
                        cp.getOptions().getBoolean(chatChannel.getName()) &&
                        !packet.blacklist.contains(cp.getUniqueId())) {
                    cp.sendMessage(message);
                }
            }
        }
    }

    public static void sendTitle(ChatChannel channel, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (CorePlayer cp : Core.getInstance().getPlayers().getAllHere()) {
            if (cp.getOptions().getBoolean(channel.getName())
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
     * @param msg Message
     */
    public static void sendTell(CorePlayer sender, CorePlayer target, String msg) {
        BaseComponent baseComponent = new TextComponent(Chat.DEFAULT + "[me -> ");
        baseComponent.addExtra(target.getChatName());
        baseComponent.addExtra(new TextComponent(Chat.DEFAULT + "] " + Chat.WHISPER + msg));
        sender.sendMessage(baseComponent);
        Core.getInstance().sendPacket(new PacketSpigotChatTell(sender.getUniqueId(), target.getUniqueId(), msg));
    }

    public static void receiveTell(CorePlayer sender, CorePlayer target, String msg) {
        BaseComponent baseComponent = new TextComponent(Chat.DEFAULT + "[");
        baseComponent.addExtra(sender.getChatName());
        baseComponent.addExtra(new TextComponent(Chat.DEFAULT + " - > me] " + Chat.WHISPER + msg));
        target.sendMessage(baseComponent);
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
