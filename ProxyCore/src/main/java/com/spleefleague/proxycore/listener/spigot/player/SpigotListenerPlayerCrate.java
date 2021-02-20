package com.spleefleague.proxycore.listener.spigot.player;

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
        ProxyCorePlayer pvp = ProxyCore.getInstance().getPlayers().get(packet.uuid);
        pvp.getCrates().changeCrateCount(packet.crateName, packet.amount);
        if (packet.opened) {
            pvp.getCrates().addOpenedCrates(packet.crateName, -packet.amount);
        }
    }

}
