package com.spleefleague.coreapi.utils.packet.bungee.refresh;

import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.shared.QueueContainerInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBungeeRefreshAll extends PacketBungee {

    public List<UUID> players;
    public List<QueueContainerInfo> queueInfoList;

    public PacketBungeeRefreshAll() { }

    public PacketBungeeRefreshAll(Collection<? extends DBPlayer> players, List<QueueContainerInfo> queueInfoList) {
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
