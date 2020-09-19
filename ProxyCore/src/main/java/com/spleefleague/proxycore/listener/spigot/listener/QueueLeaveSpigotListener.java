package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.utils.packet.spigot.PacketQueueLeave;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueManager;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class QueueLeaveSpigotListener extends SpigotListener<PacketQueueLeave> {

    @Override
    protected void receive(Connection sender, PacketQueueLeave packet) {
        if (QueueManager.leaveAllQueues(packet.player)) {
            ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(packet.player), ChatColor.GRAY + "You have left all queues");
        } else {
            ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(packet.player), ChatColor.RED + "You have nothing to leave!");
        }
    }

}
