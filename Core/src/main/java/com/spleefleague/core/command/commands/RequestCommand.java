/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.request.RequestManager;

/**
 * @author NickM13
 */
public class RequestCommand extends CommandTemplate {
    
    public RequestCommand() {
        super(RequestCommand.class, "request", Rank.DEFAULT);
        setUsage("Not for personal use");
    }
    
    @CommandAnnotation(hidden=true)
    public void requestAccept(CorePlayer sender,
            @LiteralArg(value="accept") String test,
            String target) {
        if (!RequestManager.acceptRequest(sender, target)) {
            error(sender, "No pending request");
        }
    }
    
    @CommandAnnotation(hidden=true)
    public void requestDecline(CorePlayer sender,
            @LiteralArg(value="decline") String test,
            String target) {
        if (!RequestManager.declineRequest(sender, target)) {
            error(sender, "No pending request");
        }
    }

}
