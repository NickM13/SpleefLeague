package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class PacketSpigotPlayerJoinOther extends PacketSpigot {

    public UUID sender;
    public UUID target;

    public PacketSpigotPlayerJoinOther() {

    }

    public PacketSpigotPlayerJoinOther(UUID sender, UUID target) {
        this.sender = sender;
        this.target = target;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PLAYER_JOIN_SERVER;
    }

}
