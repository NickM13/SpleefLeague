/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 */
public class SurrenderCommand extends CoreCommand {

    public SurrenderCommand() {
        super("surrender", CoreRank.DEFAULT);
        this.addAlias("ff");
    }

    @CommandAnnotation
    public void surrender(CorePlayer sender) {
        if (!sender.isInBattle() || sender.getBattleState() != BattleState.BATTLER) {
            error(sender, CoreError.NOT_INGAME);
            return;
        }
        sender.getBattle().surrender(sender);
    }

}
