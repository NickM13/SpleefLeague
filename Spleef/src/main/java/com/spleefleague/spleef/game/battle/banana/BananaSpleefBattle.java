/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.banana;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.bonanza.BonanzaBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.util.SpleefUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author NickM13
 */
public class BananaSpleefBattle extends BonanzaBattle<BananaSpleefArena, BananaSpleefPlayer> {

    protected static long FIELD_RESET = 30 * 1000L;
    protected long fieldResetTime = 0;
    protected BananaSpleefPlayer bountyPlayer = null;
    
    protected List<Vector> possibleSpawns;
    
    public BananaSpleefBattle(List<CorePlayer> players,
                              BananaSpleefArena arena) {
        super(Spleef.getInstance(), players, arena, BananaSpleefPlayer.class);
        
        possibleSpawns = new ArrayList<>();
        for (BuildStructure structure : arena.getFields()) {
            for (BlockPosition pos : structure.getFakeBlocks().keySet()) {
                possibleSpawns.add(new Vector(pos.getX(), pos.getY(), pos.getZ()));
            }
        }
    }

    public Location getRandomSpawn() {
        Random r = new Random();
        Vector spawn = possibleSpawns.get(Math.abs(r.nextInt()) % possibleSpawns.size());
        Location spawnLoc = new Location(gameWorld.getWorld(), spawn.getX(), spawn.getY() + 1, spawn.getZ());
        double theta = Math.atan2(spawn.getZ() - arena.getCenter().z, spawn.getX() - arena.getCenter().x);
        spawnLoc.setYaw((float) Math.toDegrees(theta) + 90);
        return spawnLoc;
    }
    
    @Override
    public void updateScoreboard() {
        // TODO: Probably dont need to check if bounty player is a battler, just remove on leavePlayer
        if (bountyPlayer == null || !battlers.containsKey(bountyPlayer.getCorePlayer())) {
            bountyPlayer = null;
            chatGroup.setTeamDisplayName("Bounty", "Bounty: None");
        } else {
            for (BananaSpleefPlayer bp : battlers.values()) {
                if (bp.getCorePlayer().equals(bountyPlayer.getCorePlayer())) {
                    chatGroup.setTeamDisplayNamePersonal(bp.getCorePlayer(), "Bounty", "Bounty: You!");
                } else {
                    chatGroup.setTeamDisplayNamePersonal(bp.getCorePlayer(), "Bounty", "Bounty: "
                            + Chat.PLAYER_NAME + bountyPlayer.getCorePlayer().getName()
                            + Chat.SCORE + " (" + bountyPlayer.getKnockoutStreak() + ")");
                }
            }
        }
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        chatGroup.setTeamDisplayName("PlayerCount", "Players: " + Chat.SCORE + battlers.size());

        for (BananaSpleefPlayer bp : battlers.values()) {
            chatGroup.setTeamDisplayNamePersonal(bp.getCorePlayer(), "PKnockout", "Streak: " + Chat.SCORE + ((BananaSpleefPlayer) bp).getKnockoutStreak());
        }
    }

    /*
    protected float getResetPercent() {
        return Math.max(Math.min((fieldResetTime - System.currentTimeMillis()) / (float) FIELD_RESET, 1.f), 0.f);
    }
    */
    
    @Override
    public void updateExperience() {
        //chatGroup.setExperience(getResetPercent(), 0/*(int) (Math.abs(fieldResetTime - System.currentTimeMillis()) / 1000L)*/);
    }
    
    @Override
    public void updateField() {
        if (System.currentTimeMillis() > fieldResetTime) {
            fieldResetTime = System.currentTimeMillis() + FIELD_RESET;
            fillField();
        }
    }
    
    @Override
    protected void startCountdown() {
        countdown = 0;
        gameWorld.setEditable(true);
    }
    
    @Override
    protected void fillField() {
        SpleefUtils.fillFieldFast(this);
    }

    @Override
    protected void endRound(BananaSpleefPlayer winner) {

    }

    @Override
    protected void endBattle(BananaSpleefPlayer winner) {

    }

    @Override
    protected void sendStartMessage() {
        
    }

    @Override
    protected void setupBaseSettings() {
        super.setupBaseSettings();
        chatGroup.addTeam("Bounty", "Bounty: ");
        chatGroup.addTeam("PlayerCount", Chat.SCORE + "Player Count");
        chatGroup.addTeam("PKnockout", Chat.SCORE + "");
    }

    /**
     * Adds a knockout point to a player and controls any streak
     * shutdowns or reaching required streak points actions
     *
     * @param closest Closest player
     * @param knockedOut Knocked out player
     */
    protected void addKnockout(BananaSpleefPlayer closest, BananaSpleefPlayer knockedOut) {
        closest.addKnockouts(1);

        KnockoutStreak newStreak = KnockoutStreak.getStreak(closest.getKnockoutStreak());
        KnockoutStreak endedStreak = KnockoutStreak.getStreakMin(knockedOut.getKnockoutStreak());

        if (endedStreak != null) {
            chatGroup.sendTitle(closest.getCorePlayer().getDisplayName(), endedStreak.getEndedMessage(), 5, 10, 5);
        } else if (newStreak != null) {
            chatGroup.sendTitle(closest.getCorePlayer().getDisplayName(), newStreak.getReachMessage(), 5, 10, 5);
        }

        if (bountyPlayer == null
                || !battlers.containsKey(bountyPlayer.getCorePlayer())
                || ((BananaSpleefPlayer) battlers.get(bountyPlayer.getCorePlayer())).getKnockoutStreak() < closest.getKnockoutStreak()) {
            bountyPlayer = closest;
        }
    }

    /**
     * @param cp Battling Core Player
     */
    @Override
    protected void failBattler(CorePlayer cp) {
        BananaSpleefPlayer bp = battlers.get(cp);
        gameWorld.doFailBlast(cp);
        BananaSpleefPlayer closest = battlers.get(getClosestBattler(cp));
        if (closest != null) {
            addKnockout(closest, bp);
        }
        bp.respawn();
        updateScoreboard();
    }
    
}
