package com.spleefleague.core.listener.bungee.chat;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.chat.PacketBungeeChatTell;
import org.bukkit.entity.Player;

public class BungeeListenerChatTell extends BungeeListener<PacketBungeeChatTell> {

    @Override
    protected void receive(Player sender, PacketBungeeChatTell packet) {
        Chat.receiveTell(
                Core.getInstance().getPlayers().get(packet.sender),
                Core.getInstance().getPlayers().get(packet.target),
                packet.message);
    }

}
