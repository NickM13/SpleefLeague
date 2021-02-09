package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class PacketSpigotPlayerRank extends PacketSpigot {

    public UUID uuid;
    public String rankName;
    public long duration;

    public PacketSpigotPlayerRank() {

    }

    public PacketSpigotPlayerRank(UUID uuid, String rankName, long duration) {
        this.uuid = uuid;
        this.rankName = rankName;
        this.duration = duration;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PLAYER_RANK;
    }

}
