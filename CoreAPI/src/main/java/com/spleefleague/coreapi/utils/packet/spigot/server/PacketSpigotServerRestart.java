package com.spleefleague.coreapi.utils.packet.spigot.server;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;

/**
 * @author NickM13
 * @since 2/5/2021
 */
public class PacketSpigotServerRestart extends PacketSpigot {

    public PacketSpigotServerRestart() {

    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.SERVER_RESTART;
    }

}
