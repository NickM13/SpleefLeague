/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 */
public class CancelAllCommand extends CoreCommand {
    
    public CancelAllCommand() {
        super("cancelall", CoreRank.SENIOR_MODERATOR);
        setUsage("/cancelall");
        setDescription("Cancel all ongoing matches");
    }
    
    /**
     * Moderatively cancels all battles
     * TODO: Find better way to do this
     * 
     * @param sender Core Player
     */
    @CommandAnnotation
    public void cancelall(CorePlayer sender) {
        Set<Battle> battles = new HashSet<>();
        for (CorePlayer cp : Core.getInstance().getPlayers().getAllHere()) {
            battles.add(cp.getBattle());
        }
        for (Battle battle : battles) {
            battle.cancel();
        }
        success(sender, "Cancelled all battles");
    }

}
