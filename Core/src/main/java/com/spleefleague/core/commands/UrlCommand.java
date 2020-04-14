package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author NickM13
 */
public class UrlCommand extends CommandTemplate {
    
    public UrlCommand() {
        super(UrlCommand.class, "url", Rank.MODERATOR);
    }
    
    @CommandAnnotation
    public void url(CorePlayer sender, CorePlayer cp) {
        cp.allowUrl();
        success(sender, "Allowing " + cp.getDisplayName() + " to send a url");
        success(cp, "You can send one url in the next 30 seconds");
    }

}
