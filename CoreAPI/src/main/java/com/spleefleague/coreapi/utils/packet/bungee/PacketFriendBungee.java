package com.spleefleague.coreapi.utils.packet.bungee;

import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketFriendSpigot;

import java.util.UUID;

/**
 * @author NickM13
 */
public class PacketFriendBungee extends PacketBungee {

    public PacketFriendSpigot.FriendType type;
    public UUID sender, receiver;

    public PacketFriendBungee() {

    }

    public PacketFriendBungee(PacketFriendSpigot.FriendType type, UUID sender, UUID receiver) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.FRIEND.ordinal();
    }

}
