/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 */
public class CancelAllCommand extends CommandTemplate {
    
    public CancelAllCommand() {
        super(CancelAllCommand.class, "cancelall", Rank.SENIOR_MODERATOR);
        setUsage("/cancelall");
        setDescription("Cancel all ongoing matches");
    }
    
    @CommandAnnotation
    public void cancelall(CorePlayer sender) {
        Set<Battle> battles = new HashSet<>();
        for (CorePlayer cp : Core.getInstance().getPlayers().getAll()) {
            battles.add(CorePlugin.getBattleGlobal(cp.getPlayer()));
        }
        for (Battle b : battles) {
            b.cancel();
        }
        success(sender, "Cancelled all battles");
    }

}
