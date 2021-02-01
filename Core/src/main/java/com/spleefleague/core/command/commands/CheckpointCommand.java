/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 */
public class CheckpointCommand extends CoreCommand {
    
    public CheckpointCommand() {
        super("checkpoint", CoreRank.DEFAULT);
        setUsage("/checkpoint");
        setDescription("Teleport to your checkpoint");
    }
    
    @CommandAnnotation
    public void checkpoint(CorePlayer sender) {
        sender.checkpoint();
    }

}
