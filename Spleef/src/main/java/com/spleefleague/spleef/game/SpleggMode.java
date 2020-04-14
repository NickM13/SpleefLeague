/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.spleef.game.splegg.classic.*;
import com.spleefleague.spleef.game.splegg.multi.*;
import com.spleefleague.core.game.ArenaMode;

/**
 * @author NickM13
 */
public enum SpleggMode {
    
    CLASSIC,
    MULTI;
    
    public static void init() {
        ArenaMode.addArenaMode("SPLEGG_CLASSIC", "Classic Splegg", 2, 2, ArenaMode.TeamStyle.MULTI_STATIC, false, ClassicSpleggArena.class, ClassicSpleggBattle.class);
        ArenaMode.addArenaMode("SPLEGG_MULTI", "Multisplegg", 3, 32, ArenaMode.TeamStyle.MULTI_DYNAMIC, true, MultiSpleggArena.class, MultiSpleggBattle.class);
    }
    
    public ArenaMode getArenaMode() {
        return ArenaMode.getArenaMode("SPLEGG_" + this.name());
    }
    
}
