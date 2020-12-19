package com.spleefleague.core.listener.bungee.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.PacketTellBungee;
import org.bukkit.entity.Player;

public class TellBungeeListener extends BungeeListener<PacketTellBungee> {

    @Override
    protected void receive(Player sender, PacketTellBungee packet) {
        Chat.receiveTell(
                Core.getInstance().getPlayers().get(packet.sender),
                Core.getInstance().getPlayers().get(packet.target),
                packet.message);
    }

}
