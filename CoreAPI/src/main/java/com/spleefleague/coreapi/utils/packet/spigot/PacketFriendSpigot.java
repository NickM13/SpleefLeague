package com.spleefleague.coreapi.utils.packet.spigot;

import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 */
public class PacketFriendSpigot extends PacketSpigot {

    public enum FriendType {
        ADD, REMOVE
    }

    public FriendType type;
    public UUID sender, receiver;

    public PacketFriendSpigot() {

    }

    public PacketFriendSpigot(FriendType type, UUID sender, UUID receiver) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.FRIEND.ordinal();
    }

}
