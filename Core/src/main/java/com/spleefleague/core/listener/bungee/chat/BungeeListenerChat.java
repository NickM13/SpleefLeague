package com.spleefleague.core.listener.bungee.chat;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.chat.PacketBungeeChat;
import org.bukkit.entity.Player;

public class BungeeListenerChat extends BungeeListener<PacketBungeeChat> {

    @Override
    protected void receive(Player sender, PacketBungeeChat packet) {
        Chat.sendMessage(packet);
    }

}
