package com.spleefleague.proxycore.listener.spigot.battle;

import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshQueue;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshScore;
import com.spleefleague.coreapi.utils.packet.shared.QueueContainerInfo;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleEndRated;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueContainer;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerBattleEndRated extends SpigotListener<PacketSpigotBattleEndRated> {

    @Override
    protected void receive(Connection sender, PacketSpigotBattleEndRated packet) {
        ProxyCore.getInstance().sendPacket(new PacketBungeeRefreshScore(packet.mode, packet.season, packet.players));
        for (RatedPlayerInfo rpi : packet.players) {
            ProxyCore.getInstance().getLeaderboards().get(packet.mode).getLeaderboards().get(packet.season).setPlayerScore(rpi.uuid, rpi.elo);
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(rpi.uuid);
            if (pcp != null && pcp.getPlayer() != null) {
                pcp.setBattleContainer(null);
            }
        }
        QueueContainer container = ProxyCore.getInstance().getQueueManager().getContainerMap().get(packet.mode);
        ProxyCore.getInstance().sendPacket(new PacketBungeeRefreshQueue(new QueueContainerInfo(packet.mode,
                container.getQueueSize(), container.getPlaying().size(), container.getSpectating().size())));
    }

}
