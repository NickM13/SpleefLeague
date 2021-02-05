package com.spleefleague.coreapi.utils.packet.spigot.chat;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotChatFriend extends PacketSpigot {

    public String channel;
    public String message;
    public Set<UUID> targets;

    public PacketSpigotChatFriend() { }

    public PacketSpigotChatFriend(String channel, String message, Set<UUID> targets) {
        this.channel = channel;
        this.message = message;
        this.targets = targets;
    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.CHAT_FRIEND;
    }

}
