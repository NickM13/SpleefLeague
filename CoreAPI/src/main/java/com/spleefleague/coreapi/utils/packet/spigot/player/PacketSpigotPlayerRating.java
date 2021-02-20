package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;

import javax.annotation.Nonnull;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketSpigotPlayerRating extends PacketSpigot {

    public String mode;
    public String season;
    public RatedPlayerInfo rpi;

    public PacketSpigotPlayerRating() { }

    public PacketSpigotPlayerRating(String mode, String season, RatedPlayerInfo rpi) {
        this.mode = mode;
        this.season = season;
        this.rpi = rpi;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PLAYER_RATING;
    }

}
