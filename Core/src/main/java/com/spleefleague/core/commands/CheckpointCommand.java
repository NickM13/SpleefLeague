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
public class CheckpointCommand extends CommandTemplate {
    
    public CheckpointCommand() {
        super(CheckpointCommand.class, "checkpoint", Rank.DEFAULT);
        setUsage("/checkpoint");
        setDescription("Teleport to your checkpoint");
    }
    
    @CommandAnnotation
    public void checkpoint(CorePlayer sender) {
        sender.checkpoint();
    }

}
