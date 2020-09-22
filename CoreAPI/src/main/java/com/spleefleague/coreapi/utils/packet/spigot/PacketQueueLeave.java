package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketQueueLeave extends PacketSpigot {

    public UUID player;

    public PacketQueueLeave() { }

    public PacketQueueLeave(UUID player) {
        this.player = player;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.QUEUE_LEAVE.ordinal();
    }

}
