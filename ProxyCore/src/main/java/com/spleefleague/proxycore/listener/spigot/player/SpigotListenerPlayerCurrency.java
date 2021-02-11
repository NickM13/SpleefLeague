package com.spleefleague.proxycore.listener.spigot.player;

import com.google.common.collect.Lists;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshScore;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCurrency;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerRating;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public class SpigotListenerPlayerCurrency extends SpigotListener<PacketSpigotPlayerCurrency> {

    @Override
    protected void receive(Connection sender, PacketSpigotPlayerCurrency packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.uuid);
        switch (packet.action) {
            case SET:
                pcp.getPurse().setCurrency(packet.type.name(), packet.amount);
                break;
            case CHANGE:
                pcp.getPurse().addCurrency(packet.type.name(), packet.amount);
                break;
        }
    }

}
