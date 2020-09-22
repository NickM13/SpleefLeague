/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.classic;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 */
public class ClassicSJBattle extends VersusBattle<ClassicSJPlayer> {
    
    public ClassicSJBattle(List<UUID> players, Arena arena) {
        super(SuperJump.getInstance(), players, arena, ClassicSJPlayer.class, SJMode.CLASSIC.getBattleMode());
    }
    
    @Override
    protected void setupBaseSettings() {

    }

    @Override
    protected void applyRewards(ClassicSJPlayer classicSJPlayer) {

    }

    @Override
    protected void winBattler(CorePlayer corePlayer) {

    }
    
    @Override
    public void reset() {

    }

}
