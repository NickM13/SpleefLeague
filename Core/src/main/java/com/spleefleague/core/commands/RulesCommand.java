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
public class RulesCommand extends CommandTemplate {
    
    public RulesCommand() {
        super(RulesCommand.class, "rules", Rank.DEFAULT);
        setDescription("Read the rules of the server");
    }
    
    @CommandAnnotation
    public void rules(CorePlayer sender) {
        //error(sender, CoreError.SETUP);
        success(sender, "1. Be excellent to each other");
        success(sender, "2. Party on dudes!");
    }

}
