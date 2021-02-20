package com.spleefleague.coreapi.utils.packet.spigot.ticket;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/18/2021
 */
public class PacketSpigotTicketOpen extends PacketSpigot {

    public UUID sender;
    public String message;

    public PacketSpigotTicketOpen() {

    }

    public PacketSpigotTicketOpen(UUID sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.TICKET_OPEN;
    }

}
