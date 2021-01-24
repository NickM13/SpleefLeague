package com.spleefleague.core.listener.bungee.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.PacketServerList;
import com.spleefleague.coreapi.utils.packet.bungee.PacketTellBungee;
import org.bukkit.entity.Player;

public class ServerListBungeeListener extends BungeeListener<PacketServerList> {

    @Override
    protected void receive(Player sender, PacketServerList packet) {
        Core.getInstance().updateServerList(packet);
    }

}
