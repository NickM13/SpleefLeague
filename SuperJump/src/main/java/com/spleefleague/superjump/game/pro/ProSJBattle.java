/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.pro;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.superjump.game.Jumps.Jump;
import com.spleefleague.superjump.game.SJBattle;
import java.util.List;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class ProSJBattle extends SJBattle<ProSJArena> {
    
    protected List<FakeBlock> fakeBlocks;
    
    protected List<Jump> jumps;
    protected int nextJumpIndex;
    protected int totalJumps = 25;
    protected Dimension nextJumpArea;
    protected Point nextJumpPoint;
    protected int forwardVision = 1;

    public ProSJBattle(List<CorePlayer> players, ProSJArena arena) {
        super(players, arena);
    }
    
    @Override
    protected void fillField() {
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
    }
    
    @Override
    protected void winPlayer(CorePlayer cp) {
        //resetPlayers();
        //gameWorld.clear();
        //fillField();

        nextJumpIndex++;
        if (nextJumpIndex < totalJumps) {
            FakeBlock fb;
            if (nextJumpIndex + forwardVision < totalJumps) {
                fb = fakeBlocks.get(nextJumpIndex + forwardVision);
                gameWorld.setBlock(fb.getBlockPosition(), fb.getBlockData());
            }
            fb = fakeBlocks.get(nextJumpIndex - 1);
            gameWorld.setBlock(fb.getBlockPosition(), fb.getBlockData());
            fb = fakeBlocks.get(nextJumpIndex);
            gameWorld.setBlock(fb.getBlockPosition(), Material.REDSTONE_LAMP.createBlockData());
            goals.clear();
            goals.add(new Dimension(new Point(fb.getBlockPosition().toVector()).add(-0.3, 0, -0.3), new Point(fb.getBlockPosition().toVector()).add(1.3, 4, 1.3)));
        } else {
            chatGroup.sendMessage("You completed SJ Pro (" + arena.getName() + ") in " + this.getRuntimeString());
            endBattle();
        }
    }

}
