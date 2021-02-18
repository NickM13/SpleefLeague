package com.spleefleague.proxycore.listener.spigot.server;

import com.spleefleague.coreapi.utils.packet.spigot.server.PacketSpigotServerHub;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerServerHub extends SpigotListener<PacketSpigotServerHub> {

    @Override
    protected void receive(Connection sender, PacketSpigotServerHub packet) {
        ServerInfo lobby = ProxyCore.getInstance().getLobbyServers().get(0);
        if (lobby == null) {
            ProxyCore.getInstance().getLogger().severe("No lobby server is available!");
            return;
        }

        for (UUID uuid : packet.players) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            if (pcp != null && pcp.getPlayer() != null) {
                pcp.connect(lobby);
                pcp.setCurrentBattle(null);
                pcp.setBattling(false);
                pcp.setSpectating(false);
            }
        }
    }

}
