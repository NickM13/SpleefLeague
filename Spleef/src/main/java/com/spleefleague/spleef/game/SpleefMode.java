/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.spleef.game.battle.team.*;
import com.spleefleague.spleef.game.battle.power.*;
import com.spleefleague.spleef.game.battle.multi.*;
import com.spleefleague.spleef.game.battle.classic.*;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.spleef.game.battle.banana.*;

/**
 * @author NickM13
 */
public enum SpleefMode {
    
    CLASSIC,
    TEAM,
    MULTI,
    POWER,
    WC,
    BONANZA;
    
    public static void init() {
        ArenaMode.addArenaMode("SPLEEF_CLASSIC", "Classic Spleef", 2, 2, ArenaMode.TeamStyle.VERSUS, false, ClassicSpleefArena.class, ClassicSpleefBattle.class);
        ArenaMode.addArenaMode("SPLEEF_TEAM", "Team Spleef", 2, 2, ArenaMode.TeamStyle.TEAM, false, TeamSpleefArena.class, TeamSpleefBattle.class);
        ArenaMode.addArenaMode("SPLEEF_MULTI", "Multispleef", 3, 32, ArenaMode.TeamStyle.DYNAMIC, true, MultiSpleefArena.class, MultiSpleefBattle.class);
        ArenaMode.addArenaMode("SPLEEF_BONANZA", "Bananaspleef", 3, 32, ArenaMode.TeamStyle.BONANZA, true, BananaSpleefArena.class, BananaSpleefBattle.class);
        ArenaMode.addArenaMode("SPLEEF_POWER", "Power Spleef", 2, 2, ArenaMode.TeamStyle.VERSUS, false, PowerSpleefArena.class, PowerSpleefBattle.class);
        ArenaMode.addArenaMode("SPLEEF_WC", "SWC", 2, 2, ArenaMode.TeamStyle.VERSUS, false, null, null);
    }
    
    public ArenaMode getArenaMode() {
        return ArenaMode.getArenaMode("SPLEEF_" + this.name());
    }
    
}
