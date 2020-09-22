/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game;

import com.spleefleague.core.game.BattleMode;
import com.spleefleague.splegg.game.classic.*;
import com.spleefleague.splegg.game.multi.MultiSpleggBattle;

/**
 * @author NickM13
 */
public enum SpleggMode {

    VERSUS, MULTI;
    
    private static final String prefix = "splegg:";
    
    public static void init() {
        BattleMode.addArenaMode(VERSUS.getName(), "Splegg Versus", 2, 2, BattleMode.TeamStyle.VERSUS, false, ClassicSpleggBattle.class);
        BattleMode.addArenaMode(MULTI.getName(), "Multisplegg", 3, 32, BattleMode.TeamStyle.DYNAMIC, false, MultiSpleggBattle.class);
    }
    
    public String getName() {
        return prefix + name().toLowerCase();
    }
    
    public BattleMode getBattleMode() {
        return BattleMode.get(getName());
    }
    
}
