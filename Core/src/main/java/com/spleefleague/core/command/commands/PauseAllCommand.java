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

/**
 * @author NickM13
 */
public class PauseAllCommand extends CommandTemplate {
    
    public PauseAllCommand() {
        super(PauseAllCommand.class, "pauseall", Rank.SENIOR_MODERATOR);
        setUsage("/pauseall");
        setDescription("Pause all queues");
    }
    
    @CommandAnnotation
    public void pauseall(CorePlayer sender) {
        error(sender, CoreError.SETUP);
    }

}
