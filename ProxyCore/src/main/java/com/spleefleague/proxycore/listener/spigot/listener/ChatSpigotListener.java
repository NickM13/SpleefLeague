package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.utils.packet.bungee.PacketChatBungee;
import com.spleefleague.coreapi.utils.packet.spigot.PacketChatSpigot;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class ChatSpigotListener extends SpigotListener<PacketChatSpigot> {

    @Override
    protected void receive(Connection sender, PacketChatSpigot packet) {
        ProxyCore.getInstance().sendPacket(new PacketChatBungee(packet));
    }

}
