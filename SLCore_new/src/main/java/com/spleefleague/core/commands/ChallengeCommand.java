/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;

/**
 * @author NickM13
 */
public class ChallengeCommand extends CommandTemplate {
    
    public ChallengeCommand() {
        super(ChallengeCommand.class, "challenge", Rank.DEFAULT);
        setUsage("/challenge <accept|decline> <player>");
        setDescription("Accept/Decline a challenge");
    }
    
    @CommandAnnotation
    public void challenge(CorePlayer sender, String ad, CorePlayer player) {
        error(sender, CoreError.SETUP);
    }

}
