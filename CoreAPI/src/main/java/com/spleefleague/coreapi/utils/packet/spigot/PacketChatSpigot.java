package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketChatSpigot extends PacketSpigot {

    public UUID sender;
    public String channel;
    public String message;
    public Set<UUID> blacklist;

    public PacketChatSpigot() { }

    public PacketChatSpigot(UUID sender, String channel, String message) {
        this(sender, channel, message, new HashSet<>());
    }

    public PacketChatSpigot(UUID sender, String channel, String message, Set<UUID> blacklist) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
        this.blacklist = blacklist;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.CHAT.ordinal();
    }

}
