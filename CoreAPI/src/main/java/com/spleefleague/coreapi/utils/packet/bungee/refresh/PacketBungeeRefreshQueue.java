package com.spleefleague.coreapi.utils.packet.bungee.refresh;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.shared.QueueContainerInfo;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBungeeRefreshQueue extends PacketBungee {

    public QueueContainerInfo queueInfo;

    public PacketBungeeRefreshQueue() { }

    public PacketBungeeRefreshQueue(QueueContainerInfo queueInfo) {
        this.queueInfo = queueInfo;
    }

    public PacketBungeeRefreshQueue(String mode, int queued, int playing, int spectating) {
        this.queueInfo = new QueueContainerInfo(mode, queued, playing, spectating);
    }

    public int getTag() {
        return PacketType.Bungee.REFRESH_QUEUE.ordinal();
    }

}
