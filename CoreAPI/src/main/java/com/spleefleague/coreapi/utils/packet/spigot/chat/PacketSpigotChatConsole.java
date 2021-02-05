package com.spleefleague.coreapi.utils.packet.spigot.chat;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotChatConsole extends PacketSpigot {

    public String channel;
    public String message;
    public Set<UUID> blacklist;
    public boolean url;

    public PacketSpigotChatConsole() { }

    public PacketSpigotChatConsole(String channel, String message, boolean url) {
        this(channel, message, new HashSet<>(), url);
    }

    public PacketSpigotChatConsole(String channel, String message, Set<UUID> blacklist, boolean url) {
        this.channel = channel;
        this.message = message;
        this.blacklist = blacklist;
        this.url = url;
    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.CHAT_CONSOLE;
    }

}
