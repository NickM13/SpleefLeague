package com.spleefleague.coreapi.utils.packet.spigot;

import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.RatedPlayerInfo;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketSetRating extends PacketSpigot {

    public String mode;
    public int season;
    public RatedPlayerInfo rpi;

    public PacketSetRating() { }

    public PacketSetRating(String mode, int season, RatedPlayerInfo rpi) {
        this.mode = mode;
        this.season = season;
        this.rpi = rpi;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.SET_RATING.ordinal();
    }

}
