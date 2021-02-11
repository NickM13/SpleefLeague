/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.chat.Chat;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.function.Function;

/**
 * @author NickM13
 */
public enum ChatChannel {

    ADMIN("Admin",
            true,
            net.md_5.bungee.api.ChatColor.RED,
            cp -> cp.getRank().hasPermission(CoreRank.ADMIN),
            null),
    BUILD("Build",
            true,
            net.md_5.bungee.api.ChatColor.GREEN,
            cp -> cp.getRank().hasPermission(CoreRank.BUILDER),
            null),
    GAMES("Games",
            true,
            net.md_5.bungee.api.ChatColor.AQUA,
            null,
            null),
    GLOBAL("Global",
            false,
            null,
            null,
            null),
    LOCAL("Local",
            false,
            net.md_5.bungee.api.ChatColor.GRAY,
            cp -> cp.getRank().hasPermission(CoreRank.ADMIN),
            null),
    LOGIN("Login",
            true,
            net.md_5.bungee.api.ChatColor.GRAY,
            cp -> cp.getRank().hasPermission(CoreRank.ADMIN),
            null),
    PARTY("Party",
            true,
            net.md_5.bungee.api.ChatColor.AQUA,
            cp -> cp.getParty() != null,
            null),
    SPLEEF("Spleef",
            true,
            net.md_5.bungee.api.ChatColor.GOLD,
            null,
            null),
    STAFF("Staff",
            true,
            net.md_5.bungee.api.ChatColor.LIGHT_PURPLE,
            cp -> cp.getRank().hasPermission(CoreRank.TEMP_MOD),
            null),
    SUPERJUMP("SuperJump",
            true,
            net.md_5.bungee.api.ChatColor.GOLD,
            null,
            null),
    TICKET("Ticket",
            true,
            net.md_5.bungee.api.ChatColor.GOLD,
            cp -> cp.getRank().hasPermission(CoreRank.TEMP_MOD),
            null),
    VIP("VIP",
            true,
            net.md_5.bungee.api.ChatColor.DARK_PURPLE,
            cp -> cp.getRank().hasPermission(CoreRank.VIP),
            null);

    private final String name;
    private final boolean global;
    private final net.md_5.bungee.api.ChatColor tagColor;
    private final Function<CorePlayer, Boolean> available;
    private final String playerChatColor;

    private final TextComponent tagComponent;
    private final TextComponent playerMessageComponent;

    ChatChannel(String name, boolean global, net.md_5.bungee.api.ChatColor tagColor, Function<CorePlayer, Boolean> available, String playerChatColor) {
        this.name = name;
        this.global = global;
        this.tagColor = tagColor;
        this.available = available;
        this.playerChatColor = playerChatColor == null ? Chat.PLAYER_CHAT : playerChatColor;

        if (tagColor != null) {
            tagComponent = new TextComponent(com.spleefleague.coreapi.chat.Chat.TAG_BRACE + "[" + tagColor + name + Chat.TAG_BRACE + "] ");
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

    public String getName() {
        return name;
    }

    public boolean isGlobal() {
        return global;
    }

    public net.md_5.bungee.api.ChatColor getTagColor() {
        return tagColor;
    }

    public boolean isShowingTag() {
        return tagColor != null;
    }

    public TextComponent getTagComponent() {
        return tagComponent;
    }

    public boolean isAvailable(CorePlayer cp) {
        return available == null || available.apply(cp);
    }

    public boolean isActive(CorePlayer cp) {
        return isAvailable(cp) && cp.getOptions().getBoolean("Chat:" + name());
    }

    public TextComponent getPlayerMessageBase() {
        return (TextComponent) playerMessageComponent.duplicate();
    }

}
