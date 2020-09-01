/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class PingCommand extends CoreCommand {

    public PingCommand() {
        super("ping", Rank.DEFAULT);
        setUsage("/ping [player]");
        setDescription("Get the ping of a player");
    }

    @CommandAnnotation
    public void ping(CorePlayer sender, CorePlayer cp) {
        success(sender, cp.getDisplayName() + "'s ping: " + cp.getPingFormatted());
    }

    @CommandAnnotation
    public void ping(CorePlayer sender) {
        success(sender, "Your ping: " + sender.getPingFormatted());
    }

}
