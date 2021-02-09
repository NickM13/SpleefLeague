package com.spleefleague.coreapi.utils.packet.bungee.server;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;

import javax.annotation.Nonnull;

/**
 * Empty packet on bungee server startup requesting basic server info like type
 * of server (lobby, minigame, etc)
 *
 * @author NickM13
 * @since 2/5/2021
 */
public class PacketBungeeServerPing extends PacketBungee {

    public PacketBungeeServerPing() {

    }

    @Nonnull
    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.SERVER_PING;
    }

}
