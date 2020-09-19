package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.utils.packet.spigot.PacketHub;
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
public class HubSpigotListener extends SpigotListener<PacketHub> {

    @Override
    protected void receive(Connection sender, PacketHub packet) {
        ServerInfo lobby = ProxyCore.getInstance().getLobbyServers().get(0);
        if (lobby == null) {
            ProxyCore.getInstance().getLogger().severe("No lobby server is available!");
            return;
        }

        for (UUID uuid : packet.players) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            if (pcp != null && pcp.getPlayer() != null) {
                pcp.transfer(lobby);
                pcp.setBattleContainer(null);
            }
        }
    }

}
