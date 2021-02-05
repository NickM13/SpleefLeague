package com.spleefleague.proxycore.listener.spigot.chat;

import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatConsole;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatPlayer;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.chat.ChatChannel;
import com.spleefleague.proxycore.chat.ProxyChat;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerChatPlayer extends SpigotListener<PacketSpigotChatPlayer> {

    @Override
    protected void receive(Connection sender, PacketSpigotChatPlayer packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.sender);
        ChatChannel channel = packet.channel.length() > 0 ? ChatChannel.valueOf(packet.channel) : null;
        ProxyCore.getInstance().getChat().sendMessage(
                pcp,
                channel,
                packet.message);
    }

}
