/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.team;

import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.game.SpleefBattleTeam;
import java.util.List;

/**
 * @author NickM13
 */
public class TeamSpleefBattle extends SpleefBattleTeam {
    
    public TeamSpleefBattle(List<DBPlayer> players, TeamSpleefArena arena) {
        super(players, arena);
    }
    
    @Override
    protected void startBattle() {
        super.startBattle();
    }
    
}
