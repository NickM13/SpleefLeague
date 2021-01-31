package com.spleefleague.proxycore.listener.spigot.chat;

import com.spleefleague.coreapi.utils.packet.bungee.chat.PacketBungeeChat;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChat;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerChat extends SpigotListener<PacketSpigotChat> {

    @Override
    protected void receive(Connection sender, PacketSpigotChat packet) {
        ProxyCore.getInstance().sendPacket(new PacketBungeeChat(packet));
    }

}
