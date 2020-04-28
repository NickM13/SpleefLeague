/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game;

import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.splegg.game.classic.*;

/**
 * @author NickM13
 */
public enum SpleggMode {
    
    CLASSIC,
    MULTI;
    
    public static void init() {
        ArenaMode.addArenaMode("SPLEGG_CLASSIC", "Classic Splegg", 2, 2, ArenaMode.TeamStyle.VERSUS, false, ClassicSpleggArena.class, ClassicSpleggBattle.class);
    }
    
    public ArenaMode getArenaMode() {
        return ArenaMode.getArenaMode("SPLEGG_" + this.name());
    }
    
}
