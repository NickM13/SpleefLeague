/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.game.Leaderboard;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class LeaderboardCommand extends CommandTemplate {
    
    public LeaderboardCommand() {
        super(LeaderboardCommand.class, "leaderboard", Rank.DEVELOPER);
        setOptions("leaderboards", cp -> Leaderboard.getLeaderboardNames());
        setOptions("lbstyles", cp -> Leaderboard.getLeaderboardStyles());
    }
    
    @CommandAnnotation
    public void leaderboardReset(CorePlayer sender,
            @LiteralArg(value="reset") String l,
            String name) {
        error(sender, CoreError.SETUP);
    }
    
}
