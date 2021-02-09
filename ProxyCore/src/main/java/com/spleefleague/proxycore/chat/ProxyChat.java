package com.spleefleague.proxycore.chat;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatEmoticons;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.ChatColor;
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

    private class FormattedPlayerMessage {

        TextComponent textComponent;
        boolean containsUrl;

        public FormattedPlayerMessage(TextComponent textComponent, boolean containsUrl) {
            this.textComponent = textComponent;
            this.containsUrl = containsUrl;
        }

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

        if (!channel.isAvailable(sender)) {
            ProxyCore.getInstance().sendMessage(sender, "You have " + channel.getDisplayName() + " muted!");
            ProxyCore.getInstance().sendMessage(sender, "To unmute, go to Menu->Options->Chat Channels");
            return;
        }

        TextComponent textComponent = new TextComponent();

        textComponent.addExtra(channel.getTagComponent());
        textComponent.addExtra(channel.isShowingTag() ? sender.getChatName() : sender.getChatNameRanked());
        textComponent.addExtra(ChatColor.GRAY + ": ");

        FormattedPlayerMessage playerMessage = formatPlayerMessage(message, channel.getPlayerMessageBase());

        if (playerMessage.containsUrl) {
            if (!sender.canSendUrl()) {
                ProxyCore.getInstance().sendMessage(sender, "Please ask for permission to send a URL");
                return;
            } else {
                sender.disallowUrl();
            }
        }

        textComponent.addExtra(playerMessage.textComponent);

        for (ProxyCorePlayer pcp : ProxyCore.getInstance().getPlayers().getAll()) {
            if (channel.isActive(pcp)) {
                pcp.getPlayer().sendMessage(textComponent);
            }
        }
    }

    private static final TextComponent BASE_TELL = new TextComponent();

    static {
        BASE_TELL.setColor(ChatColor.WHITE);
        BASE_TELL.setItalic(true);
    }

    public void sendTell(@Nonnull ProxyCorePlayer sender, @Nonnull ProxyCorePlayer receiver, String message) {
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
    }

    public void sendNotificationFriends(Set<UUID> uuids, ChatChannel channel, String message) {
        Set<ProxyCorePlayer> friends = new HashSet<>();
        List<ProxyCorePlayer> targets = new ArrayList<>();
        for (UUID uuid : uuids) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            friends.addAll(pcp.getFriends().getOnline());
            targets.add(pcp);
        }
        friends.removeAll(targets);

        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(message));
        for (ProxyCorePlayer friend : friends) {
            if (friend.getOptions().getBoolean("Friend:Notifications") && channel.isActive(friend)) {
                friend.getPlayer().sendMessage(textComponent);
            }
        }
    }

    public static void sendConfirmationButtons(ProxyCorePlayer receiver, String acceptCmd, String declineCmd) {
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

}
