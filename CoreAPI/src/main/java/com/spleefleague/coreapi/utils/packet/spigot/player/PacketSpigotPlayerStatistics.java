package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketSpigotPlayerStatistics extends PacketSpigot {

    public String parent;
    public String statName;
    public RatedPlayerInfo rpi;

    public PacketSpigotPlayerStatistics() { }

    public PacketSpigotPlayerStatistics(String parent, String statName, RatedPlayerInfo rpi) {
        this.parent = parent;
        this.statName = statName;
        this.rpi = rpi;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PLAYER_STATISTICS;
    }

}
