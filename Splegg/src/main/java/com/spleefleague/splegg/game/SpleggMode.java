/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game;

import com.spleefleague.core.game.BattleMode;
import com.spleefleague.splegg.game.classic.*;

/**
 * @author NickM13
 */
public enum SpleggMode {
    
    CLASSIC,
    MULTI;
    
    private static final String prefix = "splegg:";
    
    public static void init() {
        BattleMode.addArenaMode(CLASSIC.getName(), "Classic Splegg", 2, 2, BattleMode.TeamStyle.VERSUS, false, ClassicSpleggBattle.class);
    }
    
    public String getName() {
        return prefix + name().toLowerCase();
    }
    
    public BattleMode getBattleMode() {
        return BattleMode.get(getName());
    }
    
}
