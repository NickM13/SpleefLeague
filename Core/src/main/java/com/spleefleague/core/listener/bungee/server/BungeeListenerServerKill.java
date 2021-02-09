package com.spleefleague.core.listener.bungee.server;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.server.PacketBungeeServerKill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 * @since 2/6/2021
 */
public class BungeeListenerServerKill extends BungeeListener<PacketBungeeServerKill> {

    @Override
    protected void receive(Player sender, PacketBungeeServerKill packet) {
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            if (Core.getInstance().getPlayers().getAllOnlineExtended().isEmpty()) {
                System.exit(0);
            }
        }, 0, 20);
    }

}
