/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.classic;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.superjump.SuperJump;

import java.util.List;

/**
 * @author NickM13
 */
public class ClassicSJBattle extends VersusBattle<ClassicSJPlayer> {
    
    public ClassicSJBattle(List<CorePlayer> players, Arena arena, BattleMode battleMode) {
        super(SuperJump.getInstance(), players, arena, ClassicSJPlayer.class, battleMode);
    }
    
    @Override
    protected void setupBaseSettings() {
    
    }
    
    @Override
    protected void winBattler(CorePlayer corePlayer) {
    
    }
    
    @Override
    public void reset() {
    
    }
}
