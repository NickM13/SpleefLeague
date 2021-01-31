package com.spleefleague.coreapi.infraction;

import com.spleefleague.coreapi.chat.ChatColor;

/**
 * @author NickM13
 */
public enum InfractionType {

    UNBAN("Unban", ChatColor.GREEN),
    WARNING("Warning", ChatColor.YELLOW),
    KICK("Kick", ChatColor.GOLD),
    TEMPBAN("Tempban", ChatColor.RED),
    BAN("Ban", ChatColor.DARK_RED),
    MUTE_PUBLIC("Mute", ChatColor.GRAY),
    MUTE_SECRET("SecretMute", ChatColor.BLACK);

    private final String name;
    private final ChatColor color;

    private InfractionType(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

}
