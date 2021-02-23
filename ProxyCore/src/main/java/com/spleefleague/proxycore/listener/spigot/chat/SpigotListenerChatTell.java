package com.spleefleague.proxycore.listener.spigot.chat;

import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatTell;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class SpigotListenerChatTell extends SpigotListener<PacketSpigotChatTell> {

    @Override
    protected void receive(Connection sender, PacketSpigotChatTell packet) {
        if (packet.target == null) {
            ProxyCore.getInstance().getChat().sendReply(
                    ProxyCore.getInstance().getPlayers().get(packet.sender),
                    packet.message);
        } else {
            ProxyCore.getInstance().getChat().sendTell(
                    ProxyCore.getInstance().getPlayers().get(packet.sender),
                    ProxyCore.getInstance().getPlayers().get(packet.target),
                    packet.message);
        }
    }

}
