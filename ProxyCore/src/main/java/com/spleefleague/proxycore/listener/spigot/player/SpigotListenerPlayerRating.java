package com.spleefleague.proxycore.listener.spigot.player;

import com.google.common.collect.Lists;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshScore;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerRating;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerPlayerRating extends SpigotListener<PacketSpigotPlayerRating> {

    @Override
    protected void receive(Connection sender, PacketSpigotPlayerRating packet) {
        RatedPlayerInfo rpi = packet.rpi;
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(rpi.uuid);
        switch (rpi.action) {
            case SET:
                pcp.getRatings().setRating(packet.mode, packet.season, rpi.elo);
                break;
            case CHANGE:
                pcp.getRatings().addRating(packet.mode, packet.season, rpi.elo);
                break;
        }
        //ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeeRefreshScore(packet.mode, packet.season, Lists.newArrayList(rpi)));
        //ProxyCore.getInstance().getLeaderboards().get(packet.mode).getLeaderboards().get(packet.season).setPlayerScore(rpi.uuid, rpi.elo);
    }

}
