/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.splegg.classic;

import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.game.SpleggBattle;
import java.util.List;

/**
 * @author NickM13
 */
public class ClassicSpleggBattle extends SpleggBattle {
    
    public ClassicSpleggBattle(List<DBPlayer> players, ClassicSpleggArena arena) {
        super(players, arena);
    }
    
}
