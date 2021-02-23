package com.spleefleague.proxycore.listener.spigot.chat;

import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatBroadcast;
import com.spleefleague.proxycore.chat.ProxyChat;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/22/2021
 */
public class SpigotListenerChatBroadcast extends SpigotListener<PacketSpigotChatBroadcast> {

    @Override
    protected void receive(Connection sender, PacketSpigotChatBroadcast packet) {
        ProxyChat.broadcast(packet.message);
    }

}
