/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game.classic;

import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.splegg.Splegg;

import java.util.List;

/**
 * @author NickM13
 */
public class ClassicSpleggBattle extends VersusBattle<ClassicSpleggArena, ClassicSpleggPlayer> {
    
    public ClassicSpleggBattle(List<CorePlayer> players, ClassicSpleggArena arena) {
        super(Splegg.getInstance(), players, arena, ClassicSpleggPlayer.class);
    }

    @Override
    protected void setupBaseSettings() {

    }

    @Override
    protected void setupBattlers() {

    }

    @Override
    protected void sendStartMessage() {

    }

    @Override
    protected void fillField() {

    }

    @Override
    protected void joinBattler(CorePlayer cp) {

    }

    @Override
    protected void saveBattlerStats(CorePlayer cp) {

    }

    @Override
    protected void failBattler(CorePlayer cp) {

    }

    @Override
    protected void leaveBattler(CorePlayer cp) {

    }

    @Override
    public void updateScoreboard() {

    }

}
