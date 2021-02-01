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
public class PacketSpigotChat extends PacketSpigot {

    public UUID sender;
    public String channel;
    public String message;
    public Set<UUID> blacklist;
    public boolean url;

    public PacketSpigotChat() { }

    public PacketSpigotChat(UUID sender, String channel, String message, boolean url) {
        this(sender, channel, message, new HashSet<>(), url);
    }

    public PacketSpigotChat(UUID sender, String channel, String message, Set<UUID> blacklist, boolean url) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
        this.blacklist = blacklist;
        this.url = url;
    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.CHAT;
    }

}
