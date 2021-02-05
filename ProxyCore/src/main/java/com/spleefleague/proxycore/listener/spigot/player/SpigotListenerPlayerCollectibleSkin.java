package com.spleefleague.proxycore.listener.spigot.player;

import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCollectibleSkin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class SpigotListenerPlayerCollectibleSkin extends SpigotListener<PacketSpigotPlayerCollectibleSkin> {

    @Override
    protected void receive(Connection sender, PacketSpigotPlayerCollectibleSkin packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.uuid);
        switch (packet.action) {
            case LOCK:
                pcp.getCollectibles().removeSkin(packet.parent, packet.identifier, packet.skin);
                break;
            case UNLOCK:
                pcp.getCollectibles().addSkin(packet.parent, packet.identifier, packet.skin);
                break;
            case ACTIVE:
                if (packet.identifier.length() > 0) {
                    pcp.getCollectibles().setSkin(packet.parent, packet.identifier, packet.skin);
                } else {
                    pcp.getCollectibles().setSkin(packet.parent, packet.identifier, null);
                }
                break;
        }
    }

}
