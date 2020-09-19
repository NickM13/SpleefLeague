package com.spleefleague.proxycore.listener;

import com.spleefleague.coreapi.utils.packet.QueueContainerInfo;
import com.spleefleague.coreapi.utils.packet.bungee.PacketConnection;
import com.spleefleague.coreapi.utils.packet.bungee.PacketRefreshAll;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueContainer;
import com.spleefleague.proxycore.game.queue.QueueManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author NickM13
 * @since 6/6/2020
 */
public class ConnectionListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxyCore.getInstance().sendPacket(new PacketConnection(PacketConnection.ConnectionType.CONNECT, event.getPlayer().getUniqueId()));

        ProxyCore.getInstance().getPlayers().onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxyCore.getInstance().sendPacket(new PacketConnection(PacketConnection.ConnectionType.DISCONNECT, event.getPlayer().getUniqueId()));

        ProxyCore.getInstance().getPlayers().onPlayerQuit(event.getPlayer());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxyCore.getInstance().getPlayers().get(event.getPlayer().getUniqueId()).setCurrentServer(event.getPlayer().getServer().getInfo());

        if (event.getPlayer().getServer().getInfo().getPlayers().size() == 1) {
            List<QueueContainerInfo> queueInfoList = new ArrayList<>();
            for (Map.Entry<String, QueueContainer> entry : QueueManager.getContainerMap().entrySet()) {
                queueInfoList.add(new QueueContainerInfo(entry.getKey(),
                        entry.getValue().getQueueSize(),
                        entry.getValue().getPlaying().size(),
                        entry.getValue().getSpectating().size()));
            }

            ProxyCore.getInstance().sendPacket(
                    event.getPlayer().getServer().getInfo(),
                    new PacketRefreshAll(ProxyCore.getInstance().getPlayers().getAll(), queueInfoList));
        }
    }

}
