package com.spleefleague.proxycore.listener.spigot.server;

import com.spleefleague.coreapi.utils.packet.spigot.server.PacketSpigotServerPing;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.droplet.DropletType;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/5/2021
 */
public class SpigotListenerServerPing extends SpigotListener<PacketSpigotServerPing> {

    public SpigotListenerServerPing() {

    }

    @Override
    protected void receive(Connection sender, PacketSpigotServerPing packet) {
        //ProxyCore.getInstance().getDropletManager().onPingReceive(DropletType.valueOf(packet.serverType), sender.getSocketAddress());
    }

}
