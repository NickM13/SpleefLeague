package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.utils.packet.bungee.PacketTellBungee;
import com.spleefleague.coreapi.utils.packet.spigot.PacketTellSpigot;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class TellSpigotListener extends SpigotListener<PacketTellSpigot> {

    @Override
    protected void receive(Connection sender, PacketTellSpigot packet) {
        ProxyCore.getInstance().sendPacket(ProxyCore.getInstance().getPlayers().get(packet.target).getCurrentServer(), new PacketTellBungee(packet));
    }

}
