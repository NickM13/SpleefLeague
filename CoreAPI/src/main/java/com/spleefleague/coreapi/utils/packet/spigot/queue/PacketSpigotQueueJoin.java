package com.spleefleague.coreapi.utils.packet.spigot.queue;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotQueueJoin extends PacketSpigot {

    public UUID player;
    public String mode;
    public String query;

    public PacketSpigotQueueJoin() { }

    public PacketSpigotQueueJoin(UUID player, String mode, String query) {
        this.player = player;
        this.mode = mode;
        this.query = query;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.QUEUE_JOIN;
    }

}
