package com.spleefleague.proxycore.listener.spigot.player;

import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.infraction.InfractionManager;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerInfraction;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class SpigotListenerPlayerInfraction extends SpigotListener<PacketSpigotPlayerInfraction> {

    @Override
    protected void receive(Connection sender, PacketSpigotPlayerInfraction packet) {
        ProxyCore.getInstance().getInfractions().push(packet.getInfraction());
    }

}
