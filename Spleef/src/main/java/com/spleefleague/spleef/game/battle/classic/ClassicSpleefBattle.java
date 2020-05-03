/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.classic;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.util.SpleefUtils;

import java.util.List;

/**
 * @author NickM13
 */
public class ClassicSpleefBattle extends VersusBattle<ClassicSpleefPlayer> {
    
    public ClassicSpleefBattle(List<CorePlayer> players, Arena arena) {
        super(Spleef.getInstance(), players, arena, ClassicSpleefPlayer.class, SpleefMode.CLASSIC.getBattleMode());
    }
    
    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleefUtils.setupBaseSettings(this);
    }
    
    @Override
    public void fillField() {
        SpleefUtils.fillFieldFast(this);
    }
    
    @Override
    public void reset() {
        fillField();
    }
    
    @Override
    public void updateField() {
    
    }

}
