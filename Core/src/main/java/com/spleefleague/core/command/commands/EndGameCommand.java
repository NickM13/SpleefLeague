/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class EndGameCommand extends CoreCommand {
    
    public EndGameCommand() {
        super("endgame", Rank.DEFAULT);
    }
    
    @CommandAnnotation
    public void endgame(CorePlayer sender) {
        if (!sender.isInBattle()) {
            error(sender, CoreError.NOT_INGAME);
            return;
        }
        sender.getBattle().onRequest(sender, "endgame", null);
    }

}
