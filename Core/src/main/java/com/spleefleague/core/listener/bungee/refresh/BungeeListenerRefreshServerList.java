package com.spleefleague.core.listener.bungee.refresh;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshServerList;
import org.bukkit.entity.Player;

public class BungeeListenerRefreshServerList extends BungeeListener<PacketBungeeRefreshServerList> {

    @Override
    protected void receive(Player sender, PacketBungeeRefreshServerList packet) {
        Core.getInstance().updateServerList(packet);
    }

}
