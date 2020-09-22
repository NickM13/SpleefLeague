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
public class PacketQueueJoin extends PacketSpigot {

    public UUID player;
    public String mode;
    public String query;

    public PacketQueueJoin() { }

    public PacketQueueJoin(UUID player, String mode, String query) {
        this.player = player;
        this.mode = mode;
        this.query = query;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.QUEUE_JOIN.ordinal();
    }

}
