package com.spleefleague.coreapi.utils.packet.spigot.ticket;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/18/2021
 */
public class PacketSpigotTicketReply extends PacketSpigot {

    public UUID sender;
    public UUID target;
    public String msg;

    public PacketSpigotTicketReply() {

    }

    public PacketSpigotTicketReply(UUID sender, UUID target, String msg) {
        this.sender = sender;
        this.target = target;
        this.msg = msg;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.TICKET_REPLY;
    }

}
