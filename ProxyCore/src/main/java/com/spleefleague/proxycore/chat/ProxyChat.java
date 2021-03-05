package com.spleefleague.proxycore.chat;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatEmoticons;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerSound;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyDBPlayer;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public class ProxyChat {

    private static final Pattern URL_PATTERN = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    private static class FormattedPlayerMessage {

        TextComponent textComponent;
        boolean containsUrl;

        public FormattedPlayerMessage(TextComponent textComponent, boolean containsUrl) {
            this.textComponent = textComponent;
            this.containsUrl = containsUrl;
        }

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

    private FormattedPlayerMessage formatPlayerMessage(String message, TextComponent baseFormat) {
        TextComponent textComponent = baseFormat.duplicate();

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

    public void sendMessage(ProxyCorePlayer sender, ChatChannel channel, String message) {
        if (sender == null) return;
        if (channel == null) channel = sender.getChatChannel();

        TextComponent textComponent = new TextComponent();

        textComponent.addExtra(channel.getTagComponent());
        textComponent.addExtra(channel.isShowingTag() ? sender.getChatName() : sender.getChatNameRanked());
        textComponent.addExtra(ChatColor.GRAY + ": ");

        FormattedPlayerMessage playerMessage = formatPlayerMessage(message, channel.getPlayerMessageBase());

        textComponent.addExtra(playerMessage.textComponent);

        for (ProxyCorePlayer pcp : ProxyCore.getInstance().getPlayers().getAll()) {
            if (channel.isActive(pcp) && channel.canReceive(sender, pcp)) {
                pcp.getPlayer().sendMessage(textComponent);
            }
        }
    }

    private static final TextComponent BASE_TELL = new TextComponent();

    static {
        BASE_TELL.setColor(ChatColor.WHITE);
        BASE_TELL.setItalic(true);
    }

    public void sendReply(ProxyCorePlayer sender, String message) {
        if (sender.getReply() == null) {
            ProxyCore.getInstance().sendMessageError(sender, new TextComponent("No one to reply to"));
            return;
        }
        ProxyCorePlayer receiver = ProxyCore.getInstance().getPlayers().get(sender.getReply());
        if (receiver == null) {
            ProxyCore.getInstance().sendMessageError(sender, new TextComponent("No one to reply to"));
            return;
        }
        sendTell(sender, receiver, message);
    }

    public void sendTell(@Nonnull ProxyCorePlayer sender, @Nonnull ProxyCorePlayer receiver, String message) {
        if (!receiver.getOptions().getBoolean("Friend:Messages") && !receiver.getFriends().isFriend(sender.getUniqueId())) {
            TextComponent component = new TextComponent();
            component.addExtra(receiver.getChatName());
            component.addExtra(" has messaging disabled");
            ProxyCore.getInstance().sendMessageError(sender, component);
            return;
        }

        TextComponent senderComponent = new TextComponent();

        senderComponent.addExtra(ChatColor.GRAY + "[me -> ");
        senderComponent.addExtra(receiver.getChatName());
        senderComponent.addExtra(ChatColor.GRAY + "] ");

        TextComponent receiverComponent = new TextComponent();

        receiverComponent.addExtra(ChatColor.GRAY + "[");
        receiverComponent.addExtra(sender.getChatName());
        receiverComponent.addExtra(ChatColor.GRAY + " -> me] ");

        FormattedPlayerMessage playerMessage = formatPlayerMessage(message, BASE_TELL);

        senderComponent.addExtra(playerMessage.textComponent);
        receiverComponent.addExtra(playerMessage.textComponent);

        sender.getPlayer().sendMessage(senderComponent);
        receiver.getPlayer().sendMessage(receiverComponent);
        ProxyCore.getInstance().getPacketManager().sendPacket(receiver, new PacketBungeePlayerSound(receiver.getUniqueId(), "ENTITY_EXPERIENCE_ORB_PICKUP"));

        receiver.setReply(sender.getUniqueId());
    }

    public void sendNotificationFriends(Set<UUID> uuids, String message) {
        sendNotificationFriends(uuids, new TextComponent(TextComponent.fromLegacyText(message)));
    }

    public void sendNotificationFriends(Set<UUID> uuids, TextComponent component) {
        Set<ProxyCorePlayer> friends = new HashSet<>();
        List<ProxyCorePlayer> targets = new ArrayList<>();
        for (UUID uuid : uuids) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().getOffline(uuid);
            friends.addAll(pcp.getFriends().getOnline());
            targets.add(pcp);
        }
        friends.removeAll(targets);

        for (ProxyCorePlayer friend : friends) {
            if (friend.getOptions().getBoolean("Friend:Notifications")/* && channel.isActive(friend)*/) {
                friend.getPlayer().sendMessage(component);
            }
        }
    }

    public static void sendConfirmationButtons(ProxyDBPlayer receiver, String acceptCmd, String declineCmd) {
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

        ProxyCore.getInstance().sendMessage(receiver, text);
    }

    public static void sendTitle(ChatChannel channel, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Title proxyTitle = ProxyServer.getInstance().createTitle()
                .title(new TextComponent(title))
                .subTitle(new TextComponent(subtitle))
                .fadeIn(fadeIn)
                .stay(stay)
                .fadeOut(fadeOut);
        for (ProxyCorePlayer pcp : ProxyCore.getInstance().getPlayers().getAll()) {
            if (channel.isActive(pcp)) {
                pcp.getPlayer().sendTitle(proxyTitle);
            }
        }
    }

    /**
     * Send a title to all players, stay is based on how long message is
     * Used by /broadcast command
     *
     * @param msg Message
     */
    public static void broadcast(String msg) {
        String title, subtitle;
        String[] msgs = msg.split("\\\\n");
        title = Chat.BROADCAST + colorize(msgs[0]);
        subtitle = Chat.BROADCAST + colorize(msgs.length > 1 ? msgs[1] : "");
        ProxyChat.sendTitle(ChatChannel.GLOBAL, title, subtitle, 5, msg.length() * 2 + 20, 15);
        TextComponent component = new TextComponent();
        component.addExtra(Chat.TAG_BRACE + "[" + title + Chat.TAG_BRACE + "] " + subtitle);
        for (ProxyCorePlayer pcp : ProxyCore.getInstance().getPlayers().getAll()) {
            if (ChatChannel.GLOBAL.isActive(pcp)) {
                pcp.getPlayer().sendMessage(component);
            }
        }
    }

}
