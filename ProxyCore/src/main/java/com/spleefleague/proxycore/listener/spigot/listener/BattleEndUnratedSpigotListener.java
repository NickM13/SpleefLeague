package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.utils.packet.QueueContainerInfo;
import com.spleefleague.coreapi.utils.packet.bungee.PacketRefreshQueue;
import com.spleefleague.coreapi.utils.packet.spigot.PacketBattleEndUnrated;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueContainer;
import com.spleefleague.proxycore.game.queue.QueueManager;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class BattleEndUnratedSpigotListener extends SpigotListener<PacketBattleEndUnrated> {

    @Override
    protected void receive(Connection sender, PacketBattleEndUnrated packet) {
        for (UUID uuid : packet.players) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            if (pcp != null && pcp.getPlayer() != null) {
                pcp.setBattleContainer(null);
            }
        }
        QueueContainer container = QueueManager.getContainerMap().get(packet.mode);
        ProxyCore.getInstance().sendPacket(new PacketRefreshQueue(new QueueContainerInfo(packet.mode,
                container.getQueueSize(), container.getPlaying().size(), container.getSpectating().size())));
    }

}
