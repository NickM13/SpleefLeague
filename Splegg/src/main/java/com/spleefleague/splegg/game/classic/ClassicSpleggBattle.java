/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game.classic;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggMode;
import com.spleefleague.splegg.util.SpleggUtils;

import java.util.List;

/**
 * @author NickM13
 */
public class ClassicSpleggBattle extends VersusBattle<ClassicSpleggPlayer> {
    
    public ClassicSpleggBattle(List<CorePlayer> players, Arena arena) {
        super(Splegg.getInstance(), players, arena, ClassicSpleggPlayer.class, SpleggMode.CLASSIC.getBattleMode());
    }
    
    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleggUtils.setupBaseSettings(this);
    }
    
    @Override
    public void fillField() {
        SpleggUtils.fillFieldFast(this);
    }
    
    @Override
    public void reset() {
        fillField();
    }
    
    @Override
    public void updateField() {
    
    }

}
