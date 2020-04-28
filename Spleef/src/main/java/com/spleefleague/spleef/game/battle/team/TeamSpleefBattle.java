/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.team;

import com.spleefleague.core.game.battle.team.TeamBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.Spleef;

import java.util.List;

/**
 * @author NickM13
 */
public class TeamSpleefBattle extends TeamBattle<TeamSpleefArena, TeamSpleefPlayer> {
    
    public TeamSpleefBattle(List<CorePlayer> players, TeamSpleefArena arena) {
        super(Spleef.getInstance(), players, arena, TeamSpleefPlayer.class);
    }

    @Override
    protected void joinBattler(CorePlayer dbPlayer) {

    }

    @Override
    protected void endRound(TeamSpleefPlayer winner) {
    
    }

    @Override
    protected void endBattle(TeamSpleefPlayer winner) {

    }
    
}
