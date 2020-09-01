/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.request.RequestManager;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 */
public class RequestCommand extends CoreCommand {
    
    public RequestCommand() {
        super("request", Rank.DEFAULT);
        setUsage("Sends requests while in battle");
        setOptions("requests", RequestCommand::getAvailableRequests);
    }
    
    protected static Set<String> getAvailableRequests(CorePlayer cp) {
        Set<String> availableRequests = new HashSet<>();
        if (cp.isInBattle()) {
            availableRequests.addAll(cp.getBattle().getAvailableRequests(cp));
        }
        return availableRequests;
    }
    
    @CommandAnnotation
    public void request(CorePlayer sender,
            @OptionArg(listName="requests") String requestType,
            @Nullable String requestValue) {
        if (sender.isInBattle()) {
            sender.getBattle().onRequest(sender, requestType, requestValue);
        }
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
