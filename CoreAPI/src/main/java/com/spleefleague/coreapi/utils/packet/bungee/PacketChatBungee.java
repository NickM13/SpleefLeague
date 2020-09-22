package com.spleefleague.coreapi.utils.packet.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketChatSpigot;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketChatBungee extends PacketBungee {

    public UUID sender;
    public String channel;
    public String message;
    public Set<UUID> blacklist;

    public PacketChatBungee() { }

    public PacketChatBungee(UUID sender, String channel, String message) {
        this(sender, channel, message, new HashSet<>());
    }

    public PacketChatBungee(UUID sender, String channel, String message, Set<UUID> blacklist) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
        this.blacklist = blacklist;
    }

    public PacketChatBungee(PacketChatSpigot packet) {
        this.sender = packet.sender;
        this.channel = packet.channel;
        this.message = packet.message;
        this.blacklist = packet.blacklist;
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.CHAT.ordinal();
    }

}
