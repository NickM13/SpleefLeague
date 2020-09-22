package com.spleefleague.proxycore.listener.spigot.listener;

import com.google.common.collect.Lists;
import com.spleefleague.coreapi.utils.packet.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.bungee.PacketRefreshScore;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSetRating;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.leaderboard.Leaderboards;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SetRatingSpigotListener extends SpigotListener<PacketSetRating> {

    @Override
    protected void receive(Connection sender, PacketSetRating packet) {
        RatedPlayerInfo rpi = packet.rpi;
        ProxyCore.getInstance().sendPacket(new PacketRefreshScore(packet.mode, packet.season, Lists.newArrayList(rpi)));
        Leaderboards.get(packet.mode).getLeaderboards().get(packet.season).setPlayerScore(rpi.uuid, rpi.elo);
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(rpi.uuid);
        if (pcp != null && pcp.getPlayer() != null) {
            pcp.setBattleContainer(null);
        }
    }

}
