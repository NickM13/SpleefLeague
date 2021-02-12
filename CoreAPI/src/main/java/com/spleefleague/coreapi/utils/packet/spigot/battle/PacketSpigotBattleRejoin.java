package com.spleefleague.coreapi.utils.packet.spigot.battle;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotBattleRejoin extends PacketSpigot {

    public UUID player;

    public PacketSpigotBattleRejoin() { }

    public PacketSpigotBattleRejoin(UUID player) {
        this.player = player;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.BATTLE_REJOIN;
    }

}
