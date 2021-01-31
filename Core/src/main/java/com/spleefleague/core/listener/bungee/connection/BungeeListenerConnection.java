package com.spleefleague.core.listener.bungee.connection;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.coreapi.utils.packet.bungee.connection.PacketBungeeConnection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BungeeListenerConnection extends BungeeListener<PacketBungeeConnection> {

    @Override
    protected void receive(Player sender, PacketBungeeConnection packet) {
        if (packet.type == PacketBungeeConnection.ConnectionType.CONNECT) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(packet.uuid);
            for (CorePlugin<?> plugin : CorePlugin.getAllPlugins()) {
                plugin.getPlayers().onBungeeConnect(op);
            }
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                Core.getInstance().onBungeeConnect(packet.uuid);
            }, 10L);
        } else if (packet.type == PacketBungeeConnection.ConnectionType.DISCONNECT) {
            Core.getInstance().onBungeeDisconnect(packet.uuid);
            for (CorePlugin<?> plugin : CorePlugin.getAllPlugins()) {
                plugin.getPlayers().onBungeeDisconnect(packet.uuid);
            }
        } else if (packet.type == PacketBungeeConnection.ConnectionType.FIRST_CONNECT) {
            CorePlayer cp = Core.getInstance().getPlayers().get(packet.uuid);
            Core.getInstance().sendMessage(cp, "Welcome to SpleefLeague!");
        }
    }

}
