package com.spleefleague.coreapi.utils.packet.spigot.server;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotServerDirect extends PacketSpigot {

    public UUID player;
    public String serverName;

    public PacketSpigotServerDirect() { }

    public PacketSpigotServerDirect(UUID player, String serverName) {
        this.player = player;
        this.serverName = serverName;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.SERVER_CONNECT.ordinal();
    }

}
