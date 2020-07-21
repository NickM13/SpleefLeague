/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class UnqueueCommand extends CoreCommand {

    public UnqueueCommand() {
        super("unqueue", Rank.DEFAULT);
    }
    
    @CommandAnnotation
    public void unqueue(CorePlayer sender) {
        if (Core.getInstance().unqueuePlayerGlobally(sender)) {
            success(sender, "You have left all queues");
        } else {
            error(sender, "You aren't in any queues!");
        }
    }
    
}
