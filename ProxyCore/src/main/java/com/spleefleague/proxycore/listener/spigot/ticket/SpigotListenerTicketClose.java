package com.spleefleague.proxycore.listener.spigot.ticket;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketClose;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/18/2021
 */
public class SpigotListenerTicketClose extends SpigotListener<PacketSpigotTicketClose> {

    @Override
    protected void receive(Connection sender, PacketSpigotTicketClose packet) {
        ProxyCore.getInstance().getTicketManager().closeTicket(packet.target, packet.sender);
    }

}
