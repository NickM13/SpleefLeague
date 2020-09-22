package com.spleefleague.coreapi.utils.packet.spigot;

import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/20/2020
 */
public class PacketRequeue extends PacketSpigot {

    public UUID uuid;

    public PacketRequeue() { }

    public PacketRequeue(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.REQUEUE.ordinal();
    }

}
