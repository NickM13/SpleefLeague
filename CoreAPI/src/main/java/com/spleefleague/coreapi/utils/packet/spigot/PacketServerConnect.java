package com.spleefleague.coreapi.utils.packet.spigot;

import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketServerConnect extends PacketSpigot {

    public UUID player;
    public String serverName;

    public PacketServerConnect() { }

    public PacketServerConnect(UUID player, String serverName) {
        this.player = player;
        this.serverName = serverName;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.SERVER_CONNECT.ordinal();
    }

}
