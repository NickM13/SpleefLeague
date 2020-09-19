package com.spleefleague.core.listener.bungee.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.game.leaderboard.Leaderboard;
import com.spleefleague.coreapi.utils.packet.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.bungee.PacketRefreshScore;
import org.bukkit.entity.Player;

public class RefreshScoreBungeeListener extends BungeeListener<PacketRefreshScore> {

    @Override
    protected void receive(Player sender, PacketRefreshScore packet) {
        Leaderboard leaderboard = Leaderboards.get(packet.mode).getLeaderboards().get(packet.season);
        for (RatedPlayerInfo rpi : packet.players) {
            CorePlayer cp = Core.getInstance().getPlayers().get(rpi.uuid);
            cp.getRatings().setRating(packet.mode, packet.season, rpi.elo);
            leaderboard.setPlayerScore(rpi.uuid, rpi.elo);
        }
    }

}
