package com.spleefleague.coreapi.utils.packet.spigot.server;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;

/**
 * @author NickM13
 * @since 2/5/2021
 */
public class PacketSpigotServerPing extends PacketSpigot {

    public String serverType;

    public PacketSpigotServerPing() {

    }

    public PacketSpigotServerPing(String serverType) {
        this.serverType = serverType;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.SERVER_PING;
    }

}
