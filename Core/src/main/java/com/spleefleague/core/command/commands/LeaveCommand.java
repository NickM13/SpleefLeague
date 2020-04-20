/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class LeaveCommand extends CommandTemplate {
    
    public LeaveCommand() {
        super(LeaveCommand.class, "leave", Rank.DEFAULT);
        addAlias("l");
        setUsage("/leave");
        setDescription("Leave all queues");
    }
    
    @CommandAnnotation
    public void leave(CorePlayer sender) {
        if (sender.isInBattle()) {
            sender.getBattle().leavePlayer(sender);
        } else {
            Core.getInstance().unqueuePlayerGlobally(sender);
            success(sender, "You have left all queues");
        }
    }
    
}
