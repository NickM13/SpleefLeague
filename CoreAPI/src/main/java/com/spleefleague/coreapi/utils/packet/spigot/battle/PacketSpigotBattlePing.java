package com.spleefleague.coreapi.utils.packet.spigot.battle;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotBattlePing extends PacketSpigot {

    public UUID battleId;

    public PacketSpigotBattlePing() { }

    public PacketSpigotBattlePing(UUID battleId) {
        this.battleId = battleId;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.BATTLE_PING;
    }

}
