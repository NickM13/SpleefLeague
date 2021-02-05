package com.spleefleague.core.listener.bungee.player;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class BungeeListenerPlayerResync extends BungeeListener<PacketBungeePlayerResync> {

    @Override
    protected void receive(Player sender, PacketBungeePlayerResync packet) {
        Core.getInstance().getPlayers().resync(packet.uuid, packet.fields);
    }

}
