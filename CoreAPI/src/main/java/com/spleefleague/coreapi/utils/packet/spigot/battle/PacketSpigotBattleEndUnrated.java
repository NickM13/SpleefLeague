package com.spleefleague.coreapi.utils.packet.spigot.battle;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotBattleEndUnrated extends PacketSpigot {

    public String mode;
    public List<UUID> players;

    public PacketSpigotBattleEndUnrated() { }

    public PacketSpigotBattleEndUnrated(String mode, List<UUID> players) {
        this.mode = mode;
        this.players = players;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.BATTLE_END_UNRATED.ordinal();
    }

}
