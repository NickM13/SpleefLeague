package com.spleefleague.core.listener.bungee.refresh;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.game.leaderboard.Leaderboard;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshScore;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;
import org.bukkit.entity.Player;

public class BungeeListenerRefreshScore extends BungeeListener<PacketBungeeRefreshScore> {

    @Override
    protected void receive(Player sender, PacketBungeeRefreshScore packet) {
        Leaderboard leaderboard = Core.getInstance().getLeaderboards().get(packet.mode).getLeaderboards().get(packet.season);
        for (RatedPlayerInfo rpi : packet.players) {
            CorePlayer cp = Core.getInstance().getPlayers().get(rpi.uuid);
            cp.getRatings().setRating(packet.mode, packet.season, rpi.elo);
            leaderboard.setPlayerScore(rpi.uuid, rpi.elo);
        }
    }

}
