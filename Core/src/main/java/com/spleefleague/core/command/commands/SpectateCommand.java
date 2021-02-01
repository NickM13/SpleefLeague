/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.plugin.CorePlugin;

/**
 * @author NickM13
 */
public class SpectateCommand extends CoreCommand {
    
    public SpectateCommand() {
        super("spectate", CoreRank.DEFAULT);
        setUsage("/spectate <player>");
        setDescription("Spectate a player's match");
    }

    @CommandAnnotation
    public void spectate(CorePlayer sender) {
        if (sender.isInBattle() && sender.getBattleState() == BattleState.SPECTATOR) {
            sender.getBattle().leavePlayer(sender);
            success(sender, "You are no longer spectating");
        } else {
            error(sender, CoreError.SETUP);
        }
    }
    
    @CommandAnnotation
    public void spectate(CorePlayer sender,
                         @CorePlayerArg(allowCrossServer = true) CorePlayer target) {
        if (!sender.canJoinBattle()) {
            error(sender, CoreError.INGAME);
        } else {
            if (sender.isInBattle()) {
                sender.getBattle().leavePlayer(sender);
            }
            CorePlugin.spectatePlayerGlobal(sender, target);
        }
    }

}
