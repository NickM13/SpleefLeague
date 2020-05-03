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
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class PauseCommand extends CommandTemplate {
    
    public PauseCommand() {
        super(PauseCommand.class, "pause", Rank.DEFAULT);
    }
    
    @CommandAnnotation
    public void pause(CorePlayer sender, Integer time) {
        if (!sender.isInBattle()) error(sender, CoreError.NOT_INGAME);
        sender.getBattle().onRequest(sender, "pause", time.toString());
    }
    @CommandAnnotation
    public void pause(CorePlayer sender) {
        if (!sender.isInBattle()) error(sender, CoreError.NOT_INGAME);
        sender.getBattle().onRequest(sender, "pause", null);
    }
    
}
