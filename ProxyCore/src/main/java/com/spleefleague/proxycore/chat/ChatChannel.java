package com.spleefleague.proxycore.chat;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.player.ranks.ProxyRank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public enum ChatChannel {

    ADMIN("Admin",
            "仸",
            pcp -> pcp.getRank().hasPermission(ProxyRank.ADMIN),
            null),
    GAMES("Games",
            "价",
            null,
            null),
    GLOBAL("Global",
            null,
            null,
            null),
    LOCAL("Local",
            ChatColor.GRAY + "LocalChat",
            pcp -> pcp.getRank().hasPermission(ProxyRank.ADMIN),
            null),
    PARTY("Party",
            "仵",
            pcp -> pcp.getParty() != null,
            "&b",
            (sender, receiver) -> {
                return sender.getParty().getPlayerSet().contains(receiver);
            }),
    STAFF("Staff",
            "仴",
            pcp -> pcp.getRank().hasPermission(ProxyRank.MODERATOR),
            "&a"),
    TICKET("Ticket",
            "件",
            pcp -> pcp.getRank().hasPermission(ProxyRank.MODERATOR),
            null,
            null,
            false),
    VIP("VIP",
            "仹",
            pcp -> pcp.getRank().hasPermission(ProxyRank.VIP),
            "&d");

    private final String displayName;
    private final String tag;
    private final Function<ProxyCorePlayer, Boolean> available;
    private final BiFunction<ProxyCorePlayer, ProxyCorePlayer, Boolean> receive;

    private final TextComponent tagComponent;
    private final TextComponent playerMessageComponent;
    private final boolean showBaseTag;

    ChatChannel(String name,
                String tag,
                Function<ProxyCorePlayer, Boolean> available,
                String playerChatColor) {
        this(name, tag, available, playerChatColor, null);
    }

    ChatChannel(String name,
                String tag,
                Function<ProxyCorePlayer, Boolean> available,
                String playerChatColor,
                BiFunction<ProxyCorePlayer, ProxyCorePlayer, Boolean> receive) {
        this(name, tag, available, playerChatColor, receive, true);
    }

    ChatChannel(String name,
                String tag,
                Function<ProxyCorePlayer, Boolean> available,
                String playerChatColor,
                BiFunction<ProxyCorePlayer, ProxyCorePlayer, Boolean> receive,
                boolean showBaseTag) {
        this.showBaseTag = showBaseTag;
        this.displayName = name;
        this.tag = tag;
        this.available = available;
        playerChatColor = playerChatColor == null ? Chat.PLAYER_CHAT : playerChatColor;
        this.receive = receive;

        if (tag != null) {
            tagComponent = new TextComponent(tag + Chat.SPACE_1);
        } else {
            tagComponent = new TextComponent();
        }

        playerMessageComponent = new TextComponent();
        for (com.spleefleague.coreapi.chat.ChatColor chatColor : com.spleefleague.coreapi.chat.ChatColor.getChatColors(playerChatColor)) {
            switch (chatColor) {
                case RESET:
                    break;
                case STRIKETHROUGH:
                    playerMessageComponent.setStrikethrough(true);
                    break;
                case BOLD:
                    playerMessageComponent.setBold(true);
                    break;
                case UNDERLINE:
                    playerMessageComponent.setUnderlined(true);
                    break;
                case MAGIC:
                    playerMessageComponent.setObfuscated(true);
                    break;
                case ITALIC:
                    playerMessageComponent.setItalic(true);
                    break;
                default:
                    playerMessageComponent.setColor(net.md_5.bungee.api.ChatColor.getByChar(chatColor.getChar()));
                    break;
            }
        }
    }

    public boolean isShowingTag() {
        return tag != null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAvailable(ProxyCorePlayer pcp) {
        return available == null || available.apply(pcp);
    }

    public boolean isActive(ProxyCorePlayer pcp) {
        return isAvailable(pcp) && pcp.getOptions().getBoolean("Chat:" + name());
    }

    public boolean canReceive(ProxyCorePlayer sender, ProxyCorePlayer receiver) {
        if (receive != null) {
            return receive.apply(sender, receiver);
        }
        return true;
    }

    public TextComponent getTagComponent() {
        return tagComponent;
    }

    public TextComponent getPlayerMessageBase() {
        return playerMessageComponent.duplicate();
    }

    public boolean isBaseTagEnabled() {
        return showBaseTag;
    }

}
