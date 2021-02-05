package com.spleefleague.proxycore.chat;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.player.ranks.ProxyRank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.function.Function;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public enum ChatChannel {

    ADMIN("Admin",
            ChatColor.RED,
            pcp -> pcp.getRank().hasPermission(ProxyRank.ADMIN),
            null),
    BUILD("Build",
            ChatColor.GREEN,
            pcp -> pcp.getRank().hasPermission(ProxyRank.BUILDER),
            null),
    GAMES("Games",
            ChatColor.AQUA,
            null,
            null),
    GLOBAL("Global",
            null,
            null,
            null),
    LOCAL("Local",
            ChatColor.GRAY,
            pcp -> pcp.getRank().hasPermission(ProxyRank.ADMIN),
            null),
    LOGIN("Login",
            ChatColor.GRAY,
            pcp -> pcp.getRank().hasPermission(ProxyRank.ADMIN),
            null),
    PARTY("Party",
            ChatColor.AQUA,
            pcp -> pcp.getParty() != null,
            null),
    SPLEEF("Spleef",
            ChatColor.GOLD,
            null,
            null),
    STAFF("Staff",
            ChatColor.LIGHT_PURPLE,
            pcp -> pcp.getRank().hasPermission(ProxyRank.MODERATOR),
            null),
    SUPERJUMP("SuperJump",
            ChatColor.GOLD,
            null,
            null),
    TICKET("Ticket",
            ChatColor.GOLD,
            pcp -> pcp.getRank().hasPermission(ProxyRank.MODERATOR),
            null),
    VIP("VIP",
            ChatColor.DARK_PURPLE,
            pcp -> pcp.getRank().hasPermission(ProxyRank.VIP),
            null);

    private final String displayName;
    private final ChatColor tagColor;
    private final Function<ProxyCorePlayer, Boolean> available;
    private final String playerChatColor;

    private final TextComponent tagComponent;
    private final TextComponent playerMessageComponent;

    ChatChannel(String name, ChatColor tagColor, Function<ProxyCorePlayer, Boolean> available, String playerChatColor) {
        this.displayName = name;
        this.tagColor = tagColor;
        this.available = available;
        this.playerChatColor = playerChatColor == null ? Chat.PLAYER_CHAT : playerChatColor;

        if (tagColor != null) {
            tagComponent = new TextComponent(Chat.TAG_BRACE + "[" + tagColor + name + Chat.TAG_BRACE + "] ");
        } else {
            tagComponent = new TextComponent();
        }

        playerMessageComponent = new TextComponent();
        for (com.spleefleague.coreapi.chat.ChatColor chatColor : com.spleefleague.coreapi.chat.ChatColor.getChatColors(this.playerChatColor)) {
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
            }
        }
    }

    public boolean isShowingTag() {
        return tagColor != null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getTagColor() {
        return tagColor;
    }

    public boolean isAvailable(ProxyCorePlayer pcp) {
        return available == null || available.apply(pcp);
    }

    public boolean isActive(ProxyCorePlayer pcp) {
        return isAvailable(pcp) && pcp.getOptions().getBoolean("Chat:" + name());
    }

    public TextComponent getTagComponent() {
        return tagComponent;
    }

    public TextComponent getPlayerMessageBase() {
        return playerMessageComponent.duplicate();
    }

}
