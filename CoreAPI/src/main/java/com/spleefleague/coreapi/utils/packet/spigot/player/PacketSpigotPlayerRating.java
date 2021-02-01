package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketSpigotPlayerRating extends PacketSpigot {

    public String mode;
    public int season;
    public RatedPlayerInfo rpi;

    public PacketSpigotPlayerRating() { }

    public PacketSpigotPlayerRating(String mode, int season, RatedPlayerInfo rpi) {
        this.mode = mode;
        this.season = season;
        this.rpi = rpi;
    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PLAYER_RATING;
    }

}
