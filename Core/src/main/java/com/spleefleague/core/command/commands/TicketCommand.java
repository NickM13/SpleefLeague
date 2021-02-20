/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketOpen;

/**
 * @author NickM13
 */
public class TicketCommand extends CoreCommand {

    public TicketCommand() {
        super("ticket", CoreRank.DEFAULT);
    }

    @CommandAnnotation
    public void ticketOpen(CorePlayer sender,
                           @HelperArg("message") String msg) {
        Core.getInstance().sendPacket(new PacketSpigotTicketOpen(sender.getUniqueId(), msg));
    }

    /*
    @CommandAnnotation
    public void ticketView(CorePlayer sender,
                           @LiteralArg("review") String l) {
        error(sender, CoreError.SETUP);
    }

    @CommandAnnotation
    public void ticketClose(CorePlayer sender,
                            @LiteralArg("close") String l) {
        Core.getInstance().sendPacket(new PacketSpigotTicketClose(sender.getUniqueId(), sender.getUniqueId()));
    }
    */
}
