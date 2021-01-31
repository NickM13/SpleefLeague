package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotPlayerInfraction extends PacketSpigot {

    public Infraction infraction;

    public PacketSpigotPlayerInfraction() { }

    public PacketSpigotPlayerInfraction(Infraction infraction) {
        this.infraction = infraction;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.HUB.ordinal();
    }

}
