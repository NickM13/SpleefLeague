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
import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketReply;

/**
 * @author NickM13
 */
public class TicketReplyCommand extends CoreCommand {

    public TicketReplyCommand() {
        super("ticketr", CoreRank.TEMP_MOD);
        addAlias("ticketreply");
    }

    /*
    @CommandAnnotation
    public void ticketCloseOther(CorePlayer sender,
                                 @LiteralArg("close") String l,
                                 @CorePlayerArg(allowOffline = true) CorePlayer cp) {
        Core.getInstance().sendPacket(new PacketSpigotTicketClose(sender.getUniqueId(), cp.getUniqueId()));
    }
     */

    @CommandAnnotation
    public void ticketReply(CorePlayer sender,
                            CorePlayer cp,
                            String msg) {
        Core.getInstance().sendPacket(new PacketSpigotTicketReply(sender.getUniqueId(), cp.getUniqueId(), msg));
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
