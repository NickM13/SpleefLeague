package com.spleefleague.proxycore.listener.spigot.player;

import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerJoinOther;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class SpigotListenerPlayerJoinOther extends SpigotListener<PacketSpigotPlayerJoinOther> {

    @Override
    protected void receive(Connection sender, PacketSpigotPlayerJoinOther packet) {
        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(packet.sender);
        ProxyCorePlayer pcpTarget = ProxyCore.getInstance().getPlayers().get(packet.target);

        if (pcpSender == null) {
            return;
        }
        if (pcpTarget == null) {
            // Hopefully does not happen
            return;
        }
        ServerInfo info = pcpTarget.getCurrentServer();
        // TODO: Set up
    }

}
