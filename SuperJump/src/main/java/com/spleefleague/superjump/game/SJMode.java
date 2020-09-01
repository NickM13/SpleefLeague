/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game;

import com.spleefleague.core.game.BattleMode;
import com.spleefleague.superjump.game.conquest.*;
import com.spleefleague.superjump.game.endless.*;
import com.spleefleague.superjump.game.party.*;
import com.spleefleague.superjump.game.practice.*;
import com.spleefleague.superjump.game.pro.*;
import com.spleefleague.superjump.game.classic.*;
import com.spleefleague.superjump.game.shuffle.ShuffleSJBattle;

/**
 * @author NickM13
 */
public enum SJMode {
    
    CLASSIC,
    SHUFFLE,
    CONQUEST,
    ENDLESS,
    PARTY,
    PRACTICE,
    PRO;
    
    private static final String prefix = "sj:";
    
    public static void init() {
        BattleMode.addArenaMode(CLASSIC.getName(), "SuperJump: Classic", 2, 2, BattleMode.TeamStyle.VERSUS, false, ClassicSJBattle.class);
        BattleMode.addArenaMode(SHUFFLE.getName(), "SuperJump: Shuffle", 2, 2, BattleMode.TeamStyle.VERSUS, false, ShuffleSJBattle.class);
        BattleMode.addArenaMode(CONQUEST.getName(), "SuperJump: Conquest", 1, 1, BattleMode.TeamStyle.SOLO, false, ConquestSJBattle.class);
        BattleMode.addArenaMode(ENDLESS.getName(), "SuperJump: Endless", 1, 1, BattleMode.TeamStyle.SOLO, false, EndlessSJBattle.class);
        BattleMode.addArenaMode(PARTY.getName(), "SuperJump: Party", 2, 2, BattleMode.TeamStyle.VERSUS, false, PartySJBattle.class);
        BattleMode.addArenaMode(PRACTICE.getName(), "SuperJump: Practice", 1, 1, BattleMode.TeamStyle.SOLO, false, PracticeSJBattle.class);
        BattleMode.addArenaMode(PRO.getName(), "SuperJump: Pro", 1, 1, BattleMode.TeamStyle.SOLO, false, ProSJBattle.class);
    }
    
    public String getName() {
        return prefix + name().toLowerCase();
    }

    public BattleMode getBattleMode() {
        return BattleMode.get(getName());
    }
    
}
