/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;

/**
 * @author NickM13
 */
public class UnpauseAllCommand extends CommandTemplate {
    
    public UnpauseAllCommand() {
        super(UnpauseAllCommand.class, "unpauseall", Rank.SENIOR_MODERATOR);
    }
    
    @CommandAnnotation
    public void unpauseall(CorePlayer sender, String[] args) {
        error(sender, CoreError.SETUP);
    }

}
