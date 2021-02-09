package com.spleefleague.proxycore.listener.spigot.battle;

import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshQueue;
import com.spleefleague.coreapi.utils.packet.shared.QueueContainerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleEndUnrated;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueContainer;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerBattleEndUnrated extends SpigotListener<PacketSpigotBattleEndUnrated> {

    @Override
    protected void receive(Connection sender, PacketSpigotBattleEndUnrated packet) {
        for (UUID uuid : packet.players) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            if (pcp != null && pcp.getPlayer() != null) {
                pcp.setBattleContainer(null);
            }
        }
    }

}
