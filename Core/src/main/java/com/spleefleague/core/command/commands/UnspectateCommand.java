/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.plugin.CorePlugin;

/**
 * @author NickM13
 */
public class UnspectateCommand extends CoreCommand {
    
    public UnspectateCommand() {
        super("unspectate", CoreRank.DEFAULT);
        setUsage("/unspectate");
        setDescription("Stop spectating a match");
    }
    
    @CommandAnnotation
    public void unspectate(CorePlayer sender) {
        if (sender.getBattleState().equals(BattleState.SPECTATOR)) {
            CorePlugin.unspectatePlayer(sender);
            success(sender, "You are no longer spectating");
        } else {
            error(sender, "You aren't spectating a game!");
        }
    }

}
