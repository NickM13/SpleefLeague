/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;

/**
 * @author NickM13
 */
public class BackCommand extends CommandTemplate {

    public BackCommand() {
        super(BackCommand.class, "back", Rank.MODERATOR, Rank.BUILDER);
        setUsage("/back");
        setDescription("Return to a previous location");
    }
    
    @CommandAnnotation
    public void back(CorePlayer cp) {
        if (cp.getLastLocation() == null) {
            error(cp, "No place to go back to!");
        } else {
            cp.teleport(cp.getLastLocation());
            success(cp, "Teleported to your last location");
        }
    }
    
}
