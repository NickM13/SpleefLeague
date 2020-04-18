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
import com.spleefleague.core.player.Rank;

/**
 * @author NickM13
 */
public class TellCommand extends CommandTemplate {
    
    public TellCommand() {
        super(TellCommand.class, "tell", Rank.DEFAULT);
        addAlias("msg");
        addAlias("whisper");
    }
    
    @CommandAnnotation
    public void tell(CorePlayer sender,
            CorePlayer target,
            String msg) {
        Core.getInstance().sendTell(sender, target, msg);
    }
    
}
