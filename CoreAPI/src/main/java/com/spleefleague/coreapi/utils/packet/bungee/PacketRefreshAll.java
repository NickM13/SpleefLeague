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
public class PacketRefreshAll extends PacketBungee {

    public List<UUID> players;
    public List<QueueContainerInfo> queueInfoList;

    public PacketRefreshAll() { }

    public PacketRefreshAll(Collection<? extends DBPlayer> players, List<QueueContainerInfo> queueInfoList) {
        this.players = new ArrayList<>();
        for (DBPlayer dbp : players) {
            this.players.add(dbp.getUniqueId());
        }
        this.queueInfoList = queueInfoList;
    }

    public int getTag() {
        return PacketType.Bungee.REFRESH_ALL.ordinal();
    }

}
