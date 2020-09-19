package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.utils.packet.spigot.PacketForceStart;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueManager;
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
public class ForceStartSpigotListener extends SpigotListener<PacketForceStart> {

    @Override
    protected void receive(Connection sender, PacketForceStart packet) {
        List<QueuePlayer> players = new ArrayList<>();
        for (UUID uuid : packet.players) {
            players.add(new QueuePlayer(ProxyCore.getInstance().getPlayers().get(uuid), packet.query));
        }
        QueueManager.forceStart(packet.mode, packet.query, players);
    }

}
