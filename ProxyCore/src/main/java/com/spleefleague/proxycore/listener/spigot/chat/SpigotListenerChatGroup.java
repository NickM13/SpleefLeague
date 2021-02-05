package com.spleefleague.proxycore.listener.spigot.chat;

import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatConsole;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatGroup;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerChatGroup extends SpigotListener<PacketSpigotChatGroup> {

    @Override
    protected void receive(Connection sender, PacketSpigotChatGroup packet) {

    }

}
