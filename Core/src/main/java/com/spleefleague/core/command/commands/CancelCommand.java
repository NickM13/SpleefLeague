/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class CancelCommand extends CoreCommand {
    
    public CancelCommand() {
        super("cancel", Rank.SENIOR_MODERATOR);
        setUsage("/cancel <player>");
        setDescription("Cancel a player's match");
    }
    
    @CommandAnnotation
    public void cancel(CorePlayer sender) {
        if (sender.isInBattle()) {
            sender.getBattle().cancel();
            success(sender, "Match cancelled");
        } else {
            error(sender, CoreError.NOT_INGAME);
        }
    }
    
    @CommandAnnotation
    public void cancel(CorePlayer sender,
            CorePlayer target) {
        if (target.isInBattle()) {
            target.getBattle().cancel();
            success(sender, "Match cancelled");
        } else {
            error(sender, CoreError.OTHER_NOT_INGAME);
        }
    }

}
