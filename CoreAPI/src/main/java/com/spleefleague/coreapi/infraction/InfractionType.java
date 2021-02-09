package com.spleefleague.coreapi.infraction;

import com.spleefleague.coreapi.chat.ChatColor;

/**
 * @author NickM13
 */
public enum InfractionType {

    UNBAN("Unban", "ban", "$unset", ChatColor.GREEN),
    WARNING("Warning", null, null, ChatColor.YELLOW),
    KICK("Kick", null, null, ChatColor.GOLD),
    TEMPBAN("Tempban", "ban", "$set", ChatColor.RED),
    BAN("Ban", "ban", "$set", ChatColor.DARK_RED),
    UNMUTE("Unmute", "unmute", "$unset", ChatColor.AQUA),
    MUTE_PUBLIC("Mute", "mute", "$set", ChatColor.GRAY),
    MUTE_SECRET("SecretMute", "mute", "$set", ChatColor.BLACK);

    private final String name;
    private final String latestId;
    private final String latestDocOperator;
    private final ChatColor color;

    private InfractionType(String name, String latestId, String latestDocOperator, ChatColor color) {
        this.name = name;
        this.latestId = latestId;
        this.latestDocOperator = latestDocOperator;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getLatestId() {
        return latestId;
    }

    public String getLatestDocOperator() {
        return latestDocOperator;
    }

    public ChatColor getColor() {
        return color;
    }

}
