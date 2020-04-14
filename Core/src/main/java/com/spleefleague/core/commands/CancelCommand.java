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
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.database.DBPlayer;

/**
 * @author NickM13
 */
public class CancelCommand extends CommandTemplate {
    
    public CancelCommand() {
        super(CancelCommand.class, "cancel", Rank.SENIOR_MODERATOR);
        setUsage("/cancel <player>");
        setDescription("Cancel a player's match");
    }
    
    @CommandAnnotation
    public void cancel(CorePlayer sender, CorePlayer cp) {
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(cp.getPlayer());
        if (dbp != null) {
            dbp.getBattle().cancel();
            success(sender, "Match cancelled");
        } else {
            error(sender, CoreError.OTHER_NOT_INGAME);
        }
    }

}
