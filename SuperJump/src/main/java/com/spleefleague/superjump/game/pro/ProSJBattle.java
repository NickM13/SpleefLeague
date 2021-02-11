/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.pro;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.solo.SoloBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.util.Jumps_old.Jump;

import java.util.List;
import java.util.UUID;

import com.spleefleague.superjump.game.SJMode;

/**
 * @author NickM13
 */
public class ProSJBattle extends SoloBattle<ProSJPlayer> {
    
    protected List<FakeBlock> fakeBlocks;
    
    protected List<Jump> jumps;
    protected int nextJumpIndex;
    protected int totalJumps = 25;
    protected Dimension nextJumpArea;
    protected Point nextJumpPoint;
    protected int forwardVision = 1;

    public ProSJBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(SuperJump.getInstance(), battleId, players, arena, ProSJPlayer.class, SJMode.PRO.getBattleMode());
    }
    
    @Override
    protected void setupBattleRequests() {
    
    }
    
    @Override
    protected void setupScoreboard() {
    
    }
    
    @Override
    protected void fillField() {
        /*
        float difficulty;
        fakeBlocks = generate1(getSpawn(0), totalJumps, 3, true);
        
        for (int i = 0; i <= forwardVision + 1; i++) {
            FakeBlock fb = fakeBlocks.get(i);
            if (i == 0) gameWorld.setBlock(fb.getBlockPosition(), Material.REDSTONE_LAMP.createBlockData());
            else        gameWorld.setBlock(fb.getBlockPosition(), fb.getBlockData());
        }
        nextJumpIndex = 1;
        FakeBlock fb = fakeBlocks.get(nextJumpIndex);
        goals.clear();
        goals.add(new Dimension(new Point(fb.getBlockPosition().toVector()).add(-0.3, 0, -0.3), new Point(fb.getBlockPosition().toVector()).add(1.3, 4, 1.3)));
         */
    }
    
    @Override
    protected void saveBattlerStats(ProSJPlayer proSJPlayer) {
    
    }
    
    @Override
    protected void endRound(ProSJPlayer proSJPlayer) {
    
    }

    @Override
    public void reset() {
    
    }
    
    @Override
    public void setPlayTo(int i) {
    
    }

}
