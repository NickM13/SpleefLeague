/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.chat.Chat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.function.Function;

/**
 * @author NickM13
 */
public enum ChatChannel {

    ADMIN("Admin",
            net.md_5.bungee.api.ChatColor.RED,
            cp -> cp.getRank().hasPermission(CoreRank.ADMIN)),
    BUILD("Build",
            net.md_5.bungee.api.ChatColor.GREEN,
            cp -> cp.getRank().hasPermission(CoreRank.BUILDER)),
    GAMES("Games",
            net.md_5.bungee.api.ChatColor.AQUA,
            null),
    GLOBAL("Global",
            null,
            null),
    LOCAL("Local",
            net.md_5.bungee.api.ChatColor.GRAY,
            cp -> cp.getRank().hasPermission(CoreRank.ADMIN)),
    LOGIN("Login",
            net.md_5.bungee.api.ChatColor.GRAY,
            cp -> cp.getRank().hasPermission(CoreRank.ADMIN)),
    PARTY("Party",
            net.md_5.bungee.api.ChatColor.AQUA,
            cp -> cp.getParty() != null),
    SPLEEF("Spleef",
            net.md_5.bungee.api.ChatColor.GOLD,
            null),
    STAFF("Staff",
            net.md_5.bungee.api.ChatColor.LIGHT_PURPLE,
            cp -> cp.getRank().hasPermission(CoreRank.MODERATOR)),
    SUPERJUMP("SuperJump",
            net.md_5.bungee.api.ChatColor.GOLD,
            null),
    TICKET("Ticket",
            net.md_5.bungee.api.ChatColor.GOLD,
            cp -> cp.getRank().hasPermission(CoreRank.MODERATOR)),
    VIP("VIP",
            net.md_5.bungee.api.ChatColor.DARK_PURPLE,
            cp -> cp.getRank().hasPermission(CoreRank.VIP));

    private final String name;
    private final net.md_5.bungee.api.ChatColor tagColor;
    private final Function<CorePlayer, Boolean> available;

    ChatChannel(String name, net.md_5.bungee.api.ChatColor tagColor, Function<CorePlayer, Boolean> available) {
        this.name = name;
        this.tagColor = tagColor;
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public net.md_5.bungee.api.ChatColor getTagColor() {
        return tagColor;
    }

    public boolean isAvailable(CorePlayer cp) {
        return available == null || available.apply(cp);
    }

}
