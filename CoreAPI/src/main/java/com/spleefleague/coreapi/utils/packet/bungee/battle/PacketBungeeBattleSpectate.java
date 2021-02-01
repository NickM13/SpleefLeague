package com.spleefleague.coreapi.utils.packet.bungee.battle;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleSpectate;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBungeeBattleSpectate extends PacketBungee {

    public UUID spectator;
    public UUID target;

    public PacketBungeeBattleSpectate() { }

    public PacketBungeeBattleSpectate(PacketSpigotBattleSpectate packet) {
        this.spectator = packet.spectator;
        this.target = packet.target;
    }

    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.BATTLE_SPECTATE;
    }

}
