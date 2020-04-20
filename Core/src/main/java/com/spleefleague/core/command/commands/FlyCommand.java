/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class FlyCommand extends CommandTemplate {
    
    public FlyCommand() {
        super(FlyCommand.class, "fly", Rank.MODERATOR, Rank.BUILDER);
        setUsage("/fly [player]");
        setDescription("Toggle player's flight");
    }
    
    @CommandAnnotation
    public void fly(CorePlayer sender) {
        fly(sender, sender);
    }
    
    @CommandAnnotation(minRank="SENIOR_MODERATOR")
    public void fly(CorePlayer sender, CorePlayer cp) {
        cp.getPlayer().setAllowFlight(!cp.getPlayer().getAllowFlight());
        if (cp.getPlayer().getAllowFlight()) {
            if (!sender.equals(cp)) Core.getInstance().sendMessage(sender, cp.getDisplayName()  + " is now able to fly");
            Core.getInstance().sendMessage(cp, "You are now able to fly");
        } else {
            if (!sender.equals(cp)) Core.getInstance().sendMessage(sender, cp.getDisplayName() + " is no longer able to fly");
            Core.getInstance().sendMessage(cp, "You are no longer able to fly");
        }
    }

}
