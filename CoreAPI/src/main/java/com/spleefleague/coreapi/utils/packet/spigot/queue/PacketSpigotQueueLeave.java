package com.spleefleague.coreapi.utils.packet.spigot.queue;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotQueueLeave extends PacketSpigot {

    public UUID player;

    public PacketSpigotQueueLeave() { }

    public PacketSpigotQueueLeave(UUID player) {
        this.player = player;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.QUEUE_LEAVE;
    }

}
