package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.utils.packet.QueueContainerInfo;
import com.spleefleague.coreapi.utils.packet.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.bungee.PacketRefreshQueue;
import com.spleefleague.coreapi.utils.packet.bungee.PacketRefreshScore;
import com.spleefleague.coreapi.utils.packet.spigot.PacketBattleEndRated;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.leaderboard.Leaderboards;
import com.spleefleague.proxycore.game.queue.QueueContainer;
import com.spleefleague.proxycore.game.queue.QueueManager;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class BattleEndRatedSpigotListener extends SpigotListener<PacketBattleEndRated> {

    @Override
    protected void receive(Connection sender, PacketBattleEndRated packet) {
        ProxyCore.getInstance().sendPacket(new PacketRefreshScore(packet.mode, packet.season, packet.players));
        for (RatedPlayerInfo rpi : packet.players) {
            Leaderboards.get(packet.mode).getLeaderboards().get(packet.season).setPlayerScore(rpi.uuid, rpi.elo);
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(rpi.uuid);
            if (pcp != null && pcp.getPlayer() != null) {
                pcp.setBattleContainer(null);
            }
        }
        QueueContainer container = QueueManager.getContainerMap().get(packet.mode);
        ProxyCore.getInstance().sendPacket(new PacketRefreshQueue(new QueueContainerInfo(packet.mode,
                container.getQueueSize(), container.getPlaying().size(), container.getSpectating().size())));
    }

}
