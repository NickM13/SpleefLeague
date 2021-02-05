package com.spleefleague.proxycore.listener.spigot.chat;

import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatChannelJoin;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatConsole;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.chat.ChatChannel;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerChatChannelJoin extends SpigotListener<PacketSpigotChatChannelJoin> {

    @Override
    protected void receive(Connection sender, PacketSpigotChatChannelJoin packet) {
        ProxyCore.getInstance().getPlayers().get(packet.uuid).setChatChannel(ChatChannel.valueOf(packet.channelName));
    }

}
