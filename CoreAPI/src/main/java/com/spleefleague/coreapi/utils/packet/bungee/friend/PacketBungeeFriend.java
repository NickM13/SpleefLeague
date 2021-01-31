package com.spleefleague.coreapi.utils.packet.bungee.friend;

import com.spleefleague.coreapi.player.friends.FriendsAction;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.friend.PacketSpigotFriend;

import java.util.UUID;

/**
 * @author NickM13
 */
public class PacketBungeeFriend extends PacketBungee {

    public FriendsAction type;
    public UUID sender, receiver;

    public PacketBungeeFriend() {

    }

    public PacketBungeeFriend(FriendsAction type, UUID sender, UUID receiver) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.FRIEND.ordinal();
    }

}
