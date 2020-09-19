package com.spleefleague.core.listener.bungee.listener;

import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.coreapi.utils.packet.bungee.PacketConnection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ConnectionBungeeListener extends BungeeListener<PacketConnection> {

    @Override
    protected void receive(Player sender, PacketConnection packet) {
        if (packet.type == PacketConnection.ConnectionType.CONNECT) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(packet.uuid);
            for (CorePlugin<?> plugin : CorePlugin.getAllPlugins()) {
                plugin.getPlayers().onBungeeConnect(op);
            }
        } else if (packet.type == PacketConnection.ConnectionType.DISCONNECT) {
            for (CorePlugin<?> plugin : CorePlugin.getAllPlugins()) {
                plugin.getPlayers().onBungeeDisconnect(packet.uuid);
            }
        }
    }

}
