package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.utils.packet.spigot.PacketQueueJoin;
import com.spleefleague.proxycore.game.queue.QueueManager;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class QueueJoinSpigotListener extends SpigotListener<PacketQueueJoin> {

    @Override
    protected void receive(Connection sender, PacketQueueJoin packet) {
        QueueManager.joinQueue(packet.player, packet.mode, packet.query);
    }

}
