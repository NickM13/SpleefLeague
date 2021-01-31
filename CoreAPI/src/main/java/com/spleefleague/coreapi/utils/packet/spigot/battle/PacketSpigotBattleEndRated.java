package com.spleefleague.coreapi.utils.packet.spigot.battle;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;

import java.util.List;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotBattleEndRated extends PacketSpigot {

    public String mode;
    public int season;
    public List<RatedPlayerInfo> players;

    public PacketSpigotBattleEndRated() { }

    public PacketSpigotBattleEndRated(String mode, int season, List<RatedPlayerInfo> players) {
        this.mode = mode;
        this.season = season;
        this.players = players;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.BATTLE_END_RATED.ordinal();
    }

}
