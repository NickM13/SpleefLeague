package com.spleefleague.proxycore.listener.spigot.queue;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueLeave;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerQueueLeave extends SpigotListener<PacketSpigotQueueLeave> {

    @Override
    protected void receive(Connection sender, PacketSpigotQueueLeave packet) {
        if (ProxyCore.getInstance().getQueueManager().leaveAllQueues(packet.player, true)) {
            ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(packet.player), ChatColor.GRAY + "You have left all queues");
        } else {
            ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(packet.player), ChatColor.RED + "You have nothing to leave!");
        }
    }

}
