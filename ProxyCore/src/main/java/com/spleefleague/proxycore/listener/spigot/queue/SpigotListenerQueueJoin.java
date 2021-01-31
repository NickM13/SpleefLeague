package com.spleefleague.proxycore.listener.spigot.queue;

import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerQueueJoin extends SpigotListener<PacketSpigotQueueJoin> {

    @Override
    protected void receive(Connection sender, PacketSpigotQueueJoin packet) {
        ProxyCore.getInstance().getQueueManager().joinQueue(packet.player, packet.mode, packet.query);
    }

}
