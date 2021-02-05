package com.spleefleague.proxycore.listener.spigot.player;

import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCollectible;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class SpigotListenerPlayerCollectible extends SpigotListener<PacketSpigotPlayerCollectible> {

    @Override
    protected void receive(Connection sender, PacketSpigotPlayerCollectible packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.uuid);
        switch (packet.action) {
            case LOCK:
                pcp.getCollectibles().remove(packet.parent, packet.identifier);
                break;
            case UNLOCK:
                pcp.getCollectibles().add(packet.parent, packet.identifier);
                break;
            case ACTIVE:
                if (packet.identifier.length() > 0) {
                    pcp.getCollectibles().setActiveItem(packet.parent, packet.identifier, packet.affix);
                } else {
                    pcp.getCollectibles().removeActiveItem(packet.parent, packet.affix);
                }
                break;
        }
    }

}
