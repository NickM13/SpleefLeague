package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author NickM13
 */
public class UrlCommand extends CoreCommand {
    
    public UrlCommand() {
        super("url", CoreRank.MODERATOR);
    }
    
    @CommandAnnotation(disabled = true)
    public void url(CorePlayer sender, CorePlayer cp) {
        cp.allowUrl();
        success(sender, "Allowing " + cp.getDisplayName() + " to send a url");
        success(cp, "You can send one url in the next 30 seconds");
    }

}
