package com.spleefleague.proxycore.listener.spigot.chat;

import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatConsole;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerChatConsole extends SpigotListener<PacketSpigotChatConsole> {

    @Override
    protected void receive(Connection sender, PacketSpigotChatConsole packet) {

    }

}
