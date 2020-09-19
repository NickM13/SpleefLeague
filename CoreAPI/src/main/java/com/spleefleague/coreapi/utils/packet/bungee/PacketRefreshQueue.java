package com.spleefleague.coreapi.utils.packet.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.QueueContainerInfo;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketRefreshQueue extends PacketBungee {

    public QueueContainerInfo queueInfo;

    public PacketRefreshQueue() { }

    public PacketRefreshQueue(QueueContainerInfo queueInfo) {
        this.queueInfo = queueInfo;
    }

    public int getTag() {
        return PacketType.Bungee.REFRESH_QUEUE.ordinal();
    }

    public PacketRefreshQueue(String mode, int queued, int playing, int spectating) {
        this.queueInfo = new QueueContainerInfo(mode, queued, playing, spectating);
    }

    @Override
    public void fromByteArray(ByteArrayDataInput input) {
        queueInfo = new QueueContainerInfo(input);
    }

    @Override
    protected void toByteArray(ByteArrayDataOutput output) {
        queueInfo.toOutput(output);
    }

}
