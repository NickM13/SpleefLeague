package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.utils.packet.spigot.PacketRequeue;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueManager;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/20/2020
 */
public class RequeueSpigotListener extends SpigotListener<PacketRequeue> {

    @Override
    protected void receive(Connection sender, PacketRequeue packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.uuid);
        if (pcp != null) {
            if (pcp.getLastQueueRequest() != null) {
                QueueManager.joinQueue(
                        pcp.getLastQueueRequest().player,
                        pcp.getLastQueueRequest().mode,
                        pcp.getLastQueueRequest().query);
                pcp.setLastQueueRequest(null);
            } else {
                ProxyCore.getInstance().sendMessage(pcp, Chat.ERROR + "You don't have anything to requeue for!");
            }
        }
    }

}
