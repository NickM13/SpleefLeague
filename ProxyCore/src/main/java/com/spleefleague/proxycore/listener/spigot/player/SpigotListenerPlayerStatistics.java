package com.spleefleague.proxycore.listener.spigot.player;

import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerRating;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerStatistics;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerPlayerStatistics extends SpigotListener<PacketSpigotPlayerStatistics> {

    @Override
    protected void receive(Connection sender, PacketSpigotPlayerStatistics packet) {
        RatedPlayerInfo rpi = packet.rpi;
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().getOffline(rpi.uuid);
        switch (rpi.action) {
            case SET:
                pcp.getStatistics().set(packet.parent, packet.statName, rpi.elo);
                break;
            case CHANGE:
                pcp.getStatistics().add(packet.parent, packet.statName, rpi.elo);
                break;
        }
        //ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeeRefreshScore(packet.mode, packet.season, Lists.newArrayList(rpi)));
        //ProxyCore.getInstance().getLeaderboards().get(packet.mode).getLeaderboards().get(packet.season).setPlayerScore(rpi.uuid, rpi.elo);
    }

}
