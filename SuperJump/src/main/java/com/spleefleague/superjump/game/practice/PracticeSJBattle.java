/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.practice;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.solo.SoloBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 */
public class PracticeSJBattle extends SoloBattle<PracticeSJPlayer> {

    public PracticeSJBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(SuperJump.getInstance(), battleId, players, arena, PracticeSJPlayer.class, SJMode.PRACTICE.getBattleMode());
    }
    
    @Override
    protected void setupBattleRequests() {
    
    }
    
    @Override
    protected void setupScoreboard() {
    
    }
    
    @Override
    protected void saveBattlerStats(PracticeSJPlayer practiceSJPlayer) {
    
    }
    
    @Override
    protected void endRound(PracticeSJPlayer practiceSJPlayer) {
    
    }
    
    @Override
    public void reset() {
    
    }
    
    @Override
    public void setPlayTo(int i) {
    
    }
    
}
