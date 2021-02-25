package com.spleefleague.core.listener.bungee.player;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class BungeeListenerPlayerSound extends BungeeListener<PacketBungeePlayerSound> {

    @Override
    protected void receive(Player sender, PacketBungeePlayerSound packet) {
        CorePlayer cp = Core.getInstance().getPlayers().get(packet.target);
        if (cp == null || cp.getPlayer() == null) return;
        cp.getPlayer().playSound(cp.getPlayer().getLocation(), Sound.valueOf(packet.sound), 1, 1);
    }

}
