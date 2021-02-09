package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.infraction.InfractionPV;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import javax.annotation.Nonnull;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotPlayerInfraction extends PacketSpigot {

    public InfractionPV infraction;

    public PacketSpigotPlayerInfraction() { }

    public PacketSpigotPlayerInfraction(Infraction infraction) {
        this.infraction = new InfractionPV(infraction);
    }

    public Infraction getInfraction() {
        return new Infraction(infraction);
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PLAYER_INFRACTION;
    }

}
