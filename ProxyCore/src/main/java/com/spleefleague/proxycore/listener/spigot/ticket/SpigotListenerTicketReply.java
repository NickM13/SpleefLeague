package com.spleefleague.proxycore.listener.spigot.ticket;

import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketClose;
import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketReply;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/18/2021
 */
public class SpigotListenerTicketReply extends SpigotListener<PacketSpigotTicketReply> {

    @Override
    protected void receive(Connection sender, PacketSpigotTicketReply packet) {
        ProxyCore.getInstance().getTicketManager().replyTicket(packet.sender, packet.target, packet.msg);
    }

}
