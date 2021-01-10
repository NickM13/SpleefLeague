package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.utils.packet.bungee.PacketFriendBungee;
import com.spleefleague.coreapi.utils.packet.spigot.PacketFriendSpigot;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class FriendSpigotListener extends SpigotListener<PacketFriendSpigot> {

    @Override
    protected void receive(Connection sender, PacketFriendSpigot packet) {
        ProxyCore.getInstance().sendPacket(packet.receiver, new PacketFriendBungee(packet.type, packet.sender, packet.receiver));
    }

}
