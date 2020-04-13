/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.database.DBPlayer;

/**
 * @author NickM13
 */
public class PlayToCommand extends CommandTemplate {
    
    public PlayToCommand() {
        super(PlayToCommand.class, "playto", Rank.DEFAULT);
    }
    
    @CommandAnnotation
    public void playto(CorePlayer sender, Integer points) {
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(sender.getPlayer());
        if (dbp != null) {
            dbp.getBattle().requestPlayTo(dbp, points);
        }
    }
    @CommandAnnotation
    public void playto(CorePlayer sender) {
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(sender.getPlayer());
        if (dbp != null) {
            dbp.getBattle().requestPlayTo(dbp);
        }
    }
    
}
