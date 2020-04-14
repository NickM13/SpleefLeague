/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game;

import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.superjump.game.conquest.*;
import com.spleefleague.superjump.game.endless.*;
import com.spleefleague.superjump.game.party.*;
import com.spleefleague.superjump.game.practice.*;
import com.spleefleague.superjump.game.pro.*;
import com.spleefleague.superjump.game.versus.classic.*;
import com.spleefleague.superjump.game.versus.shuffle.*;

/**
 * @author NickM13
 */
public enum SJMode {
    
    CLASSIC(true),
    SHUFFLE(true),
    CONQUEST(true),
    ENDLESS(true),
    PARTY(true),
    PRACTICE(true),
    PRO(true);
    
    private final boolean queue;
    
    public static void init() {
        ArenaMode.addArenaMode("SJ_CLASSIC", "SuperJump: Classic", 2, 2, ArenaMode.TeamStyle.MULTI_STATIC, false, ClassicSJArena.class, ClassicSJBattle.class);
        ArenaMode.addArenaMode("SJ_SHUFFLE", "SuperJump: Shuffle", 2, 2, ArenaMode.TeamStyle.MULTI_STATIC, false, ShuffleSJArena.class, ShuffleSJBattle.class);
        ArenaMode.addArenaMode("SJ_CONQUEST", "SuperJump: Conquest", 1, 1, ArenaMode.TeamStyle.SOLO, false, ConquestSJArena.class, ConquestSJBattle.class);
        ArenaMode.addArenaMode("SJ_ENDLESS", "SuperJump: Endless", 1, 1, ArenaMode.TeamStyle.SOLO, false, EndlessSJArena.class, EndlessSJBattle.class);
        ArenaMode.addArenaMode("SJ_PARTY", "SuperJump: Party", 2, 2, ArenaMode.TeamStyle.MULTI_STATIC, false, PartySJArena.class, PartySJBattle.class);
        ArenaMode.addArenaMode("SJ_PRACTICE", "SuperJump: Practice", 1, 1, ArenaMode.TeamStyle.SOLO, false, PracticeSJArena.class, PracticeSJBattle.class);
        ArenaMode.addArenaMode("SJ_PRO", "SuperJump: Pro", 1, 1, ArenaMode.TeamStyle.SOLO, false, ProSJArena.class, ProSJBattle.class);
    }
    
    private SJMode(boolean queue) {
        this.queue = queue;
    }
    
    public ArenaMode getArenaMode() {
        return ArenaMode.getArenaMode("SJ_" + this.name());
    }
    
    public boolean hasQueue() {
        return queue;
    }
    
}
