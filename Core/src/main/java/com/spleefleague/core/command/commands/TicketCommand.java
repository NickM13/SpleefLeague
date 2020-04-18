/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.chat.ticket.Ticket;
import com.spleefleague.core.chat.ticket.Tickets;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import org.bukkit.OfflinePlayer;

/**
 * @author NickM13
 */
public class TicketCommand extends CommandTemplate {
    
    public TicketCommand() {
        super(TicketCommand.class, "ticket", Rank.DEFAULT);
        setUsage("/ticket <msg|player|close> [msg|player]");
    }
    
    @CommandAnnotation
    public void ticketOpen(CorePlayer sender,
            @LiteralArg(value="open") String l,
            @HelperArg(value="<message>") String msg) {
        if ((Tickets.getOpenTicket(sender) == null)) {
            Tickets.openTicket(sender, msg);
        } else {
            error(sender, "You can only have one open ticket!");
        }
    }
    
    @CommandAnnotation
    public void ticketView(CorePlayer sender,
            @LiteralArg(value="view") String l) {
        Ticket ticket = Tickets.getOpenTicket(sender);
        if (ticket != null && ticket.isOpen()) {
            // Display ticket
            ticket.sendResponses(sender);
        } else {
            error(sender, "You don't have an open ticket!");
        }
    }
    
    @CommandAnnotation(minRank="MODERATOR")
    public void ticketAll(CorePlayer sender,
            @LiteralArg(value="all") String l,
            OfflinePlayer op) {
        
    }
    
    @CommandAnnotation
    public void ticketClose(CorePlayer sender,
            @LiteralArg(value="close") String l) {
        Ticket ticket = Tickets.getOpenTicket(sender);
        if (ticket != null) {
            ticket.close(sender);
        } else {
            error(sender, "You don't have an open ticket!");
        }
    }
    
    @CommandAnnotation(minRank="MODERATOR")
    public void ticketCloseOther(CorePlayer sender,
            @LiteralArg(value="close") String l,
            CorePlayer cp) {
        Ticket ticket = Tickets.getOpenTicket(cp);
        if (ticket != null) {
            ticket.close(sender);
        } else {
            error(sender, "Is this a color issue? " + cp.getDisplayName() + " doesn't have any open tickets!");
        }
    }
    
    @CommandAnnotation(minRank="MODERATOR")
    public void ticketReply(CorePlayer sender,
            @LiteralArg(value="reply") String l,
            CorePlayer cp,
            String msg) {
        Ticket ticket = Tickets.getOpenTicket(cp);
        if (ticket != null) {
            ticket.sendMessageToSender(sender, msg);
        } else {
            error(sender, cp.getDisplayName() + " doesn't have any open tickets!");
        }
    }
    
}
