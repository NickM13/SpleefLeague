package com.spleefleague.core.listener.bungee.player;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerMute;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class BungeeListenerPlayerMute extends BungeeListener<PacketBungeePlayerMute> {

    @Override
    protected void receive(Player sender, PacketBungeePlayerMute packet) {
        CorePlayer cp = Core.getInstance().getPlayers().get(packet.target);
        if (cp == null) return;
        cp.updateMute();
    }

}
