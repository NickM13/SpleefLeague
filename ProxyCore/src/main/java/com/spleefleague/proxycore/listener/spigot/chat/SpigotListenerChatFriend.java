package com.spleefleague.proxycore.listener.spigot.chat;

import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatFriend;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.chat.ChatChannel;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class SpigotListenerChatFriend extends SpigotListener<PacketSpigotChatFriend> {

    @Override
    protected void receive(Connection sender, PacketSpigotChatFriend packet) {
        ProxyCore.getInstance().getChat().sendNotificationFriends(packet.targets, ChatChannel.valueOf(packet.channel), packet.message);
    }

}
