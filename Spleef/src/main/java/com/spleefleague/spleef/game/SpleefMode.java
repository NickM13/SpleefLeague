/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.game.BattleMode;
import com.spleefleague.spleef.game.battle.team.*;
import com.spleefleague.spleef.game.battle.power.*;
import com.spleefleague.spleef.game.battle.multi.*;
import com.spleefleague.spleef.game.battle.classic.*;
import com.spleefleague.spleef.game.battle.bonanza.*;

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
    
    private static final String prefix = "spleef:";
    
    public static void init() {
        BattleMode.addArenaMode(CLASSIC.getName(), "Classic Spleef", 2, 2, BattleMode.TeamStyle.VERSUS, false, ClassicSpleefBattle.class);
        BattleMode.addArenaMode(TEAM.getName(), "Team Spleef", 2, 2, BattleMode.TeamStyle.TEAM, false, TeamSpleefBattle.class);
        BattleMode.addArenaMode(MULTI.getName(), "Multispleef", 2, 32, BattleMode.TeamStyle.DYNAMIC, true, MultiSpleefBattle.class);
        BattleMode.addArenaMode(BONANZA.getName(), "Bonanza Spleef", 0, 0, BattleMode.TeamStyle.BONANZA, true, BonanzaSpleefBattle.class);
        BattleMode.addArenaMode(POWER.getName(), "Power Spleef", 2, 2, BattleMode.TeamStyle.VERSUS, false, PowerSpleefBattle.class);
        BattleMode.addArenaMode(WC.getName(), "SWC", 2, 2, BattleMode.TeamStyle.VERSUS, false, null);
    }
    
    public String getName() {
        return prefix + name().toLowerCase();
    }
    
    public BattleMode getBattleMode() {
        return BattleMode.get(getName());
    }
    
}
