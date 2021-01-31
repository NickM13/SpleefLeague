package com.spleefleague.coreapi.utils.packet.spigot.friend;

import com.spleefleague.coreapi.player.friends.FriendsAction;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 */
public class PacketSpigotFriend extends PacketSpigot {

    public FriendsAction type;
    public UUID sender, receiver;

    public PacketSpigotFriend() {

    }

    public PacketSpigotFriend(FriendsAction type, UUID sender, UUID receiver) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.FRIEND.ordinal();
    }

}
