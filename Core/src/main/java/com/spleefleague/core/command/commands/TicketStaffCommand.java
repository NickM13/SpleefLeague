/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketClose;
import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketReply;
import org.bukkit.OfflinePlayer;

/**
 * @author NickM13
 */
public class TicketStaffCommand extends CoreCommand {

    public TicketStaffCommand() {
        super("ticketstaff", CoreRank.MODERATOR);
    }

    @CommandAnnotation
    public void ticketAll(CorePlayer sender,
                          @LiteralArg(value = "all") String l,
                          OfflinePlayer op) {
        error(sender, CoreError.SETUP);
    }

    @CommandAnnotation
    public void ticketCloseOther(CorePlayer sender,
                                 @LiteralArg("close") String l,
                                 @CorePlayerArg(allowOffline = true) CorePlayer cp) {
        Core.getInstance().sendPacket(new PacketSpigotTicketClose(sender.getUniqueId(), cp.getUniqueId()));
    }

    @CommandAnnotation
    public void ticketReply(CorePlayer sender,
                            @LiteralArg(value = "reply") String l,
                            CorePlayer cp,
                            String msg) {
        Core.getInstance().sendPacket(new PacketSpigotTicketReply(sender.getUniqueId(), cp.getUniqueId(), msg));
    }

}
