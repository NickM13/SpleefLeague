/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.banana;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.spleef.game.SpleefBattleDynamic;
import com.spleefleague.spleef.game.spleef.SpleefBattlePlayer;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author NickM13
 */
public class BananaSpleefBattle extends SpleefBattleDynamic {

    protected static long FIELD_RESET = 30 * 1000L;
    protected long fieldResetTime = 0;
    protected BananaSpleefBattlePlayer bountyPlayer = null;
    
    protected List<Vector> possibleSpawns;
    
    public BananaSpleefBattle(List<CorePlayer> players,
                              BananaSpleefArena arena) {
        super(players, arena, BananaSpleefBattlePlayer.class);
        
        possibleSpawns = new ArrayList<>();
        for (Dimension field : arena.getField().getAreas()) {
            for (int x = (int) field.getLow().x; x <= field.getHigh().x; x++) {
                for (int y = (int) field.getLow().y; y <= field.getHigh().y; y++) {
                    for (int z = (int) field.getLow().z; z <= field.getHigh().z; z++) {
                        possibleSpawns.add(new Vector(x, y, z));
                    }
                }
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
            for (BattlePlayer bp : battlers.values()) {
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

        for (BattlePlayer bp : battlers.values()) {
            chatGroup.setTeamDisplayNamePersonal(bp.getCorePlayer(), "PKnockout", "Streak: " + Chat.SCORE + ((BananaSpleefBattlePlayer) bp).getKnockoutStreak());
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
        fillFieldFast();
    }

    @Override
    protected void endRound(BattlePlayer winner) {

    }

    @Override
    protected void endBattle(BattlePlayer winner) {

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
    protected void addKnockout(BananaSpleefBattlePlayer closest, BananaSpleefBattlePlayer knockedOut) {
        super.addKnockout(closest, knockedOut);

        KnockoutStreak newStreak = KnockoutStreak.getStreak(closest.getKnockoutStreak());
        KnockoutStreak endedStreak = KnockoutStreak.getStreakMin(knockedOut.getKnockoutStreak());

        if (endedStreak != null) {
            chatGroup.sendTitle(closest.getCorePlayer().getDisplayName(), endedStreak.getEndedMessage(), 5, 10, 5);
        } else if (newStreak != null) {
            chatGroup.sendTitle(closest.getCorePlayer().getDisplayName(), newStreak.getReachMessage(), 5, 10, 5);
        }

        if (bountyPlayer == null
                || !battlers.containsKey(bountyPlayer.getCorePlayer())
                || ((BananaSpleefBattlePlayer) battlers.get(bountyPlayer.getCorePlayer())).getKnockoutStreak() < closest.getKnockoutStreak()) {
            bountyPlayer = closest;
        }
    }

    /**
     * @param cp Battling Core Player
     */
    @Override
    protected void failBattler(CorePlayer cp) {
        for (BattlePlayer bp : battlers.values()) {
            if (bp.getCorePlayer().equals(cp)) {
                gameWorld.doFailBlast(cp);
                SpleefBattlePlayer csbp = (SpleefBattlePlayer) getClosestPlayer(bp);
                if (csbp != null) {
                    addKnockout((BananaSpleefBattlePlayer) csbp, (BananaSpleefBattlePlayer) bp);
                    sortBattlers();
                }
                bp.respawn();
                break;
            }
        }
        updateScoreboard();
    }
    
}
