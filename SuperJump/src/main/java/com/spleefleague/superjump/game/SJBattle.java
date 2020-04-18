/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.superjump.SuperJump;

import java.util.*;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author NickM13
 * @param <A>
 */
public class SJBattle<A extends SJArena> extends Battle<A> {
    
    protected Random random;
    
    protected List<Dimension> goals = new ArrayList<>();
    
    //protected final Request endGameRequest = new Request();
    
    protected long timeLastLap;
    
    public SJBattle(List<DBPlayer> players, A arena) {
        super(SuperJump.getInstance(), players, arena, SJBattlePlayer.class);
        this.gameMode = GameMode.ADVENTURE;
        goals.addAll(arena.getGoals());
        this.gameWorld.showSpectators(false);
        timeLastLap = 0;
    }

    @Override
    protected void setupBattlers() {

    }

    @Override
    protected void sendStartMessage() {

    }

    protected long getLevelTime() {
        if (this.timeLastLap == 0) return 0;
        return System.currentTimeMillis() - this.timeLastLap;
    }

    @Override
    protected void saveBattlerStats(DBPlayer dbPlayer) {

    }

    @Override
    protected void setupBaseSettings() {

    }
    
    @Override
    public void updateScoreboard() {
        
    }

    @Override
    public void updateField() {

    }

    @Override
    public void updateExperience() {

    }
    
    @Override
    public void requestEndGame(DBPlayer sjp) {
        if (!battlers.containsKey(sjp)) return;
        CorePlayer cp = Core.getInstance().getPlayers().get(sjp);
        if (battlers.size() == 1) {
            endBattle();
        } else {
            /*
            if (endGameRequest.getRequester() == null || endGameRequest.getRequester().equals(sjp)) {
                endGameRequest.setRequest(sjp);
                Core.getInstance().sendMessage(this.chatGroup, Chat.PLAYER_NAME + cp.getDisplayName() + Chat.DEFAULT + " is requesting to end the game");
            } else {
                endGameRequest.setRequest(null);
                endBattle(null);
            }
             */
        }
    }

    @Override
    protected void resetPlayer(DBPlayer dbPlayer) {

    }

    @Override
    protected void leaveBattler(DBPlayer dbPlayer) {

    }

    @Override
    protected void fillField() {

    }

    @Override
    protected void joinBattler(DBPlayer dbPlayer) {

    }

    @Override
    protected void setupBattleInventory(DBPlayer dbPlayer) {

    }

    @Override
    protected void failBattler(DBPlayer dbPlayer) {

    }

    protected boolean isInGoal(DBPlayer sjp) {
        for (Dimension goal : goals) {
            if (goal.isContained(new Point(sjp.getPlayer().getLocation()))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void releasePlayers() {
        super.releasePlayers();
        timeLastLap = System.currentTimeMillis();
    }
    
    protected void winPlayer(DBPlayer sjp) {
        
    }

    private static Point getMin(Point a, Point b) {
        return new Point(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
    }

    private static Point getMax(Point a, Point b) {
        return new Point(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
    }

    protected Jumps.Jump getNextJump(final ArrayList<Jumps.Jump> possibleJumps, final int frequencySum, float difficulty) {
        int id = random.nextInt(frequencySum);
        for (Jumps.Jump jump : possibleJumps) {
            id -= jump.getFrequency(difficulty);
            if (id < 0) {
                return jump;
            }
        }
        return null;
    }
    
    protected List<FakeBlock> generate1(Location spawn1, int jumpCount, float difficulty, boolean randomize) {
        if (randomize) random = new Random();
        goals.clear();
        borders.clear();
        
        int frequencySumB, frequencySumF, frequencySumI;
        ArrayList<Jumps.Jump> possibleJumpsB;
        
        possibleJumpsB = Jumps.getJumpsByDifficultyB(difficulty);
        frequencySumB = 0;
        for(Jumps.Jump j : possibleJumpsB) {
            frequencySumB += j.getFrequency(difficulty);
        }
        
        List<FakeBlock> fakeBlocks = new ArrayList<>();
        Point locSmallest, locHighest;
        Location lastLoc = spawn1.clone().add(0, -1, 0);
        locSmallest = locHighest = new Point(lastLoc);
        fakeBlocks.add(new FakeBlock(new BlockPosition(lastLoc.toVector()), Material.GLOWSTONE.createBlockData()));
        
        Jumps.Jump next;
        for (int j = 0; j < jumpCount; j++) {
            next = getNextJump(possibleJumpsB, frequencySumB, difficulty);
            lastLoc = next.apply(lastLoc, false, false);
            locSmallest = getMin(locSmallest, new Point(lastLoc));
            locHighest = getMax(locHighest, new Point(lastLoc));
            if (j < jumpCount - 1) {
                fakeBlocks.add(new FakeBlock(new BlockPosition(lastLoc.toVector()), Material.LIGHT_BLUE_TERRACOTTA.createBlockData()));
            } else {
                goals.add(new Dimension(new Point(lastLoc.clone().add(-0.5, 1, -0.5)), new Point(lastLoc.clone().add(0.5, 3, 0.5))));
                fakeBlocks.add(new FakeBlock(new BlockPosition(lastLoc.toVector()), Material.GLOWSTONE.createBlockData()));
            }
        }
        
        borders.add(new Dimension(locSmallest.add(-3, -3, -3), locHighest.add(3, 3, 3)));
        
        return fakeBlocks;
    }
    
    protected void generate2(Location goal, int jumpCount, int difficulty, boolean randomize) {
        // Creates a random string that is the same for everyone based on the day
        if (randomize) random = new Random();
        goals.clear();
        borders.clear();
        
        int frequencySumB, frequencySumF, frequencySumI;
        ArrayList<Jumps.Jump> possibleJumpsB;
        
        possibleJumpsB = Jumps.getJumpsByDifficultyB(difficulty);
        frequencySumB = 0;
        for(Jumps.Jump j : possibleJumpsB) {
            frequencySumB += j.getFrequency(difficulty);
        }
        
        List<FakeBlock> fakeBlocks = new ArrayList<>();
        goals.add(new Dimension(new Point(goal.clone().add(-0.5, 0, -0.5)), new Point(goal.clone().add(0.5, 2, 0.5))));
        fakeBlocks.add(new FakeBlock(new BlockPosition(goal.clone().add(0, -1, 0).toVector()), Material.GLOWSTONE.createBlockData()));
        
        List<Jumps.Jump> jumps = new ArrayList<>();
        for (int j = 0; j < jumpCount; j++) {
            jumps.add(getNextJump(possibleJumpsB, frequencySumB, difficulty));
        }
        
        Location forwardLoc, backwardLoc;
        Point locLowest, locHighest;
        forwardLoc = goal.clone().add(0, -1, 0);
        backwardLoc = goal.clone().add(0, -1, 0);
        locLowest = locHighest = new Point(forwardLoc);
        for (int j = 0; j < jumps.size(); j++) {
            Jumps.Jump jump = jumps.get(j);
            forwardLoc = jump.apply(forwardLoc, false, true);
            backwardLoc = jump.apply(backwardLoc, true, true);
            locLowest = getMin(locLowest, getMin(new Point(backwardLoc), new Point(forwardLoc)));
            locHighest = getMax(locHighest, getMax(new Point(backwardLoc), new Point(forwardLoc)));
            if (j == jumps.size() - 1) {
                fakeBlocks.add(new FakeBlock(new BlockPosition(forwardLoc.toVector()), Material.DRIED_KELP_BLOCK.createBlockData()));
                fakeBlocks.add(new FakeBlock(new BlockPosition(backwardLoc.toVector()), Material.DRIED_KELP_BLOCK.createBlockData()));
                Iterator<BattlePlayer> bit = battlers.values().iterator();
                SJBattlePlayer sjbp1 = (SJBattlePlayer) bit.next();
                SJBattlePlayer sjbp2 = (SJBattlePlayer) bit.next();
                sjbp1.setSpawn(forwardLoc.clone().add(0, 1, 0));
                sjbp1.getSpawn().setYaw(90);
                sjbp2.setSpawn(backwardLoc.clone().add(0, 1, 0));
                sjbp2.getSpawn().setYaw(270);
            } else {
                fakeBlocks.add(new FakeBlock(new BlockPosition(forwardLoc.toVector()), Material.REDSTONE_LAMP.createBlockData()));
                fakeBlocks.add(new FakeBlock(new BlockPosition(backwardLoc.toVector()), Material.REDSTONE_LAMP.createBlockData()));
            }
        }

        for (FakeBlock fb : fakeBlocks) {
            gameWorld.setBlock(fb.getBlockPosition(), fb.getBlockData());
        }
        
        borders.add(new Dimension(locLowest.add(-3, -3, -3), locHighest.add(3, 3, 3)));
    }
    
}
