/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.coreapi.utils.packet.spigot.PacketRequeue;

/**
 * @author NickM13
 */
public class RequeueCommand extends CoreCommand {

    public RequeueCommand() {
        super("requeue", Rank.DEFAULT);
    }

    @CommandAnnotation(hidden = true)
    public void requeue(CorePlayer sender) {
        Core.getInstance().sendPacket(new PacketRequeue(sender.getUniqueId()));
    }

}
