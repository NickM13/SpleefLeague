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
public class SetMaxCommand extends CommandTemplate {
    
    public SetMaxCommand() {
        super(SetMaxCommand.class, "setmax", Rank.DEVELOPER);
        setUsage("/setmax <count>");
        setDescription("Set maximum players");
    }
    
    @CommandAnnotation
    public void setmax(CorePlayer sender, Integer count) {
        error(sender, CoreError.SETUP);
    }

}
