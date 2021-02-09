package com.spleefleague.coreapi.utils.packet.spigot.queue;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/20/2020
 */
public class PacketSpigotQueueRequeue extends PacketSpigot {

    public UUID uuid;

    public PacketSpigotQueueRequeue() { }

    public PacketSpigotQueueRequeue(UUID uuid) {
        this.uuid = uuid;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.QUEUE_REQUEUE;
    }

}
