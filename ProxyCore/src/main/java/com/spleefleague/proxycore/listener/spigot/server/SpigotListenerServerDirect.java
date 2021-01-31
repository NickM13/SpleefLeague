package com.spleefleague.proxycore.listener.spigot.server;

import com.spleefleague.coreapi.utils.packet.spigot.server.PacketSpigotServerDirect;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerServerDirect extends SpigotListener<PacketSpigotServerDirect> {

    @Override
    protected void receive(Connection sender, PacketSpigotServerDirect packet) {
        ServerInfo server = ProxyCore.getInstance().getServerByName(packet.serverName);

        if (server == null) {
            ProxyCore.getInstance().getLogger().severe("Server " + packet.serverName + " not found!");
            return;
        }

        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.player);
        if (pcp != null && pcp.getPlayer() != null) {
            pcp.transfer(server);
            pcp.setBattleContainer(null);
        }
    }

}
