package com.spleefleague.proxycore.listener.spigot.battle;

import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleForceStart;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueEntity;
import com.spleefleague.proxycore.game.queue.QueuePlayer;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerBattleForceStart extends SpigotListener<PacketSpigotBattleForceStart> {

    @Override
    protected void receive(Connection sender, PacketSpigotBattleForceStart packet) {
        List<QueueEntity> players = new ArrayList<>();
        for (UUID uuid : packet.players) {
            players.add(new QueuePlayer(ProxyCore.getInstance().getPlayers().get(uuid), packet.query, 0));
        }
        ProxyCore.getInstance().getQueueManager().forceStart(packet.mode, packet.query, players);
    }

}
