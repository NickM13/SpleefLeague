package com.spleefleague.coreapi.utils.packet.bungee;

import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.QueueContainerInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketServerList extends PacketBungee {

    public List<String> serverNames;

    public PacketServerList() { }

    public PacketServerList(List<String> serverNames) {
        this.serverNames = serverNames;
    }

    public int getTag() {
        return PacketType.Bungee.SERVER_LIST.ordinal();
    }

}
