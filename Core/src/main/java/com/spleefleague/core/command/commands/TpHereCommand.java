/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import java.util.List;

/**
 * @author NickM13
 */
public class TpHereCommand extends CommandTemplate {
    
    public TpHereCommand() {
        super(TpHereCommand.class, "tphere", Rank.MODERATOR, Rank.BUILDER);
    }
    
    @CommandAnnotation
    public void tphere(CorePlayer sender, CorePlayer cp) {
        cp.teleport(sender.getLocation());
        success(cp, "Teleported to " + sender.getDisplayName());
    }
    @CommandAnnotation(minRank="DEVELOPER")
    public void tphere(CorePlayer sender, List<CorePlayer> cplayers) {
        for (CorePlayer cp : cplayers) {
            if (!cp.equals(sender)) {
                cp.teleport(sender.getLocation());
                success(cp, "Teleported to " + sender.getDisplayName());
            }
        }
    }

}
