package com.spleefleague.proxycore.listener.spigot.removal;

import com.spleefleague.coreapi.utils.packet.spigot.removal.PacketSpigotRemovalCollectible;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

import java.util.UUID;

/**
 * @author NickM13
 * @since 2/26/2021
 */
public class SpigotListenerRemovalCollectible extends SpigotListener<PacketSpigotRemovalCollectible> {

    @Override
    protected void receive(Connection sender, PacketSpigotRemovalCollectible packet) {
        ProxyCorePlayer proxyCorePlayer = ProxyCore.getInstance().getPlayers().get(packet.sender);
        for (UUID uuid : ProxyCore.getInstance().getPlayers().getAllOfflineUuids()) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().getOffline(uuid);
            if (pcp.getCollectibles().onDelete(packet.parent, packet.identifier, packet.crate)) {
                ProxyCore.getInstance().sendMessage(proxyCorePlayer, pcp.getName());
            }
        }
    }

}
