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

/**
 * @author NickM13
 */
public class AfkCommand extends CommandTemplate {
    
    public AfkCommand() {
        super(AfkCommand.class, "afk", Rank.DEFAULT);
        setUsage("/afk");
    }
    
    @CommandAnnotation
    public void afk(CorePlayer sender) {
        sender.setAfk(!sender.isAfk());
    }

}
