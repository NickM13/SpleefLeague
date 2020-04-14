/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.database.DBPlayer;

/**
 * @author NickM13
 */
public class UnspectateCommand extends CommandTemplate {
    
    public UnspectateCommand() {
        super(UnspectateCommand.class, "unspectate", Rank.DEFAULT);
        setUsage("/unspectate");
        setDescription("Stop spectating a match");
    }
    
    @CommandAnnotation
    public void unspectate(CorePlayer sender) {
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(sender.getPlayer());
        if (dbp != null && (dbp.getBattleState() == BattleState.SPECTATOR || dbp.getBattleState() == BattleState.REFEREE)) {
            CorePlugin.unspectatePlayerGlobal(sender.getPlayer());
            success(sender, "You are no longer spectating");
        } else {
            error(sender, "You aren't spectating a game");
        }
    }

}
