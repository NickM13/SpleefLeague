package com.spleefleague.proxycore.listener.spigot.player;

import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCollectibleSkin;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCrate;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class SpigotListenerPlayerCrate extends SpigotListener<PacketSpigotPlayerCrate> {

    @Override
    protected void receive(Connection sender, PacketSpigotPlayerCrate packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.uuid);
        pcp.getCrates().changeCrateCount(packet.crateName, packet.amount);
        if (packet.opened) {
            pcp.getCrates().addOpenedCrates(packet.crateName, -packet.amount);
        }
    }

}
