package com.spleefleague.proxycore.listener;

import com.spleefleague.coreapi.utils.packet.bungee.connection.PacketBungeeConnection;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshAll;
import com.spleefleague.coreapi.utils.packet.shared.QueueContainerInfo;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueContainer;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 6/6/2020
 */
public class ConnectionListener implements Listener {

    @EventHandler
    public void onPreLogin(net.md_5.bungee.api.event.LoginEvent event) {
        System.out.println("Checking for bans goes here! " + event.getConnection().getUniqueId());
        ProxyCore.getInstance().getInfractions();
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        PacketBungeeConnection packetConnection = new PacketBungeeConnection(PacketBungeeConnection.ConnectionType.CONNECT, event.getPlayer().getUniqueId());
        ProxyCore.getInstance().getPacketManager().sendPacket(packetConnection);

        ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            ProxyCore.getInstance().getPacketManager().sendPacket(event.getPlayer().getUniqueId(), new PacketBungeeConnection(PacketBungeeConnection.ConnectionType.FIRST_CONNECT, event.getPlayer().getUniqueId()));
        }, 1000, TimeUnit.MILLISECONDS);

        ProxyCore.getInstance().getPlayers().onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeeConnection(PacketBungeeConnection.ConnectionType.DISCONNECT, event.getPlayer().getUniqueId()));

        ProxyCore.getInstance().getPlayers().onPlayerQuit(event.getPlayer());

        ProxyCore.getInstance().getPartyManager().onDisconnect(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxyCorePlayer pcp  = ProxyCore.getInstance().getPlayers().get(event.getPlayer().getUniqueId());
        pcp.setCurrentServer(event.getPlayer().getServer().getInfo());

        if (event.getPlayer().getServer().getInfo().getPlayers().size() == 1) {
            List<QueueContainerInfo> queueInfoList = new ArrayList<>();
            for (Map.Entry<String, QueueContainer> entry : ProxyCore.getInstance().getQueueManager().getContainerMap().entrySet()) {
                queueInfoList.add(new QueueContainerInfo(entry.getKey(),
                        entry.getValue().getQueueSize(),
                        entry.getValue().getPlaying().size(),
                        entry.getValue().getSpectating().size()));
            }

            ProxyCore.getInstance().getPacketManager().sendPacket(
                    event.getPlayer().getServer().getInfo(),
                    new PacketBungeeRefreshAll(ProxyCore.getInstance().getPlayers().getAll(), queueInfoList));
        }

        ProxyCore.getInstance().getPartyManager().onServerSwap(pcp);
    }

}
