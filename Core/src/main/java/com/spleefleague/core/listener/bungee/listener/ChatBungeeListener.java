package com.spleefleague.core.listener.bungee.listener;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.PacketChatBungee;
import org.bukkit.entity.Player;

public class ChatBungeeListener extends BungeeListener<PacketChatBungee> {

    @Override
    protected void receive(Player sender, PacketChatBungee packet) {
        Chat.sendMessage(packet);
    }

}
