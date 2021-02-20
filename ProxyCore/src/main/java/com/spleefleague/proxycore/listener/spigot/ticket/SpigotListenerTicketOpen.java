package com.spleefleague.proxycore.listener.spigot.ticket;

import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketClose;
import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketOpen;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/18/2021
 */
public class SpigotListenerTicketOpen extends SpigotListener<PacketSpigotTicketOpen> {

    @Override
    protected void receive(Connection sender, PacketSpigotTicketOpen packet) {
        ProxyCore.getInstance().getTicketManager().openTicket(packet.sender, packet.message);
    }

}
