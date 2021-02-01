package com.spleefleague.coreapi.utils.packet.bungee.chat;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBungeeChat extends PacketBungee {

    public UUID sender;
    public String channel;
    public String message;
    public Set<UUID> blacklist;
    public boolean url;

    public PacketBungeeChat() { }

    public PacketBungeeChat(UUID sender, String channel, String message, boolean url) {
        this(sender, channel, message, new HashSet<>(), url);
    }

    public PacketBungeeChat(UUID sender, String channel, String message, Set<UUID> blacklist, boolean url) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
        this.blacklist = blacklist;
        this.url = url;
    }

    public PacketBungeeChat(PacketSpigotChat packet) {
        this.sender = packet.sender;
        this.channel = packet.channel;
        this.message = packet.message;
        this.blacklist = packet.blacklist;
        this.url = packet.url;
    }

    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.CHAT;
    }

}
