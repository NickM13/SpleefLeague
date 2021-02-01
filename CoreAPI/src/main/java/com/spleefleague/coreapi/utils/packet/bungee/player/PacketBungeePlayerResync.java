package com.spleefleague.coreapi.utils.packet.bungee.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;

import java.util.UUID;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public class PacketBungeePlayerResync extends PacketBungee {

    public enum Field {
        RANK,
        FRIENDS,
        RATINGS,
        COLLECTIBLES,
        PURSE
    }

    public Field field;
    public UUID uuid;

    public PacketBungeePlayerResync() {

    }

    public PacketBungeePlayerResync(Field field, UUID uuid) {
        this.field = field;
        this.uuid = uuid;
    }

    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.PLAYER_RESYNC;
    }

}
