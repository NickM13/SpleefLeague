/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.database.variable.DBPlayer;

/**
 * @author NickM13
 */
public class CancelCommand extends CommandTemplate {
    
    public CancelCommand() {
        super(CancelCommand.class, "cancel", Rank.SENIOR_MODERATOR);
        setUsage("/cancel <player>");
        setDescription("Cancel a player's match");
    }
    
    @CommandAnnotation
    public void cancel(CorePlayer sender, CorePlayer cp) {
        if (cp.getBattle() != null) {
            cp.getBattle().cancel();
            success(sender, "Match cancelled");
        } else {
            error(sender, CoreError.OTHER_NOT_INGAME);
        }
    }

}
