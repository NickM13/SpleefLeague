/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.team;

import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.game.SpleefBattleTeamed;
import java.util.List;

/**
 * @author NickM13
 */
public class TeamSpleefBattle extends SpleefBattleTeamed {
    
    public TeamSpleefBattle(List<CorePlayer> players, TeamSpleefArena arena) {
        super(players, arena, TeamSpleefBattlePlayer.class);
    }

    @Override
    protected void joinBattler(CorePlayer dbPlayer) {

    }

    @Override
    protected void resetPlayer(CorePlayer dbPlayer) {

    }

    @Override
    protected void endRound(BattlePlayer winner) {

    }

    @Override
    protected void endBattle(BattlePlayer winner) {

    }
}
