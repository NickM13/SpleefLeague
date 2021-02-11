/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.bonanza;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.bonanza.BonanzaBattle;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.util.SpleefUtils;
import org.bukkit.Location;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author NickM13
 */
public class BonanzaSpleefBattle extends BonanzaBattle<BonanzaSpleefPlayer> {

    protected static long FIELD_RESET = 30 * 1000L;
    protected long fieldResetTime = 0;
    protected BonanzaSpleefPlayer bountyPlayer = null;
    
    public BonanzaSpleefBattle(UUID battleId, List<UUID> players,
                               Arena arena) {
        super(Spleef.getInstance(), battleId, players, arena, BonanzaSpleefPlayer.class, SpleefMode.BONANZA.getBattleMode());
    }

    public Location getRandomSpawn() {
        Random r = new Random();
        return arena.getSpawns().get(r.nextInt(arena.getSpawns().size())).toLocation(arena.getWorld());
    }
    
    @Override
    public void updateScoreboard() {
        // TODO: Probably dont need to check if bounty player is a battler, just remove on leavePlayer
        if (bountyPlayer == null || !battlers.containsKey(bountyPlayer.getCorePlayer())) {
            bountyPlayer = null;
            chatGroup.setTeamDisplayName("Bounty", "Bounty: None");
        } else {
            for (BonanzaSpleefPlayer bp : battlers.values()) {
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

        for (BonanzaSpleefPlayer bp : battlers.values()) {
            chatGroup.setTeamDisplayNamePersonal(bp.getCorePlayer(), "PKnockout", "Streak: " + Chat.SCORE + ((BonanzaSpleefPlayer) bp).getKnockoutStreak());
        }
    }

    private float getResetPercent() {
        return Math.max(Math.min((fieldResetTime - System.currentTimeMillis()) / (float) FIELD_RESET, 1.f), 0.f);
    }
    
    @Override
    public void updateExperience() {
        chatGroup.setExperience(getResetPercent(), 0/*(int) (Math.abs(fieldResetTime - System.currentTimeMillis()) / 1000L)*/);
    }
    
    @Override
    public void updateField() {
        if (System.currentTimeMillis() > fieldResetTime) {
            fieldResetTime = System.currentTimeMillis() + FIELD_RESET;
            fillField();
        }
    }
    
    @Override
    public void startCountdown() {
        gameWorld.setEditable(true);
    }
    
    @Override
    protected void fillField() {
        SpleefUtils.fillFieldFast(this, BuildStructures.get("spleef:bonanza"));
    }
    
    @Override
    protected void saveBattlerStats(BonanzaSpleefPlayer bonanzaSpleefPlayer) {

    }
    
    @Override
    protected void endRound(BonanzaSpleefPlayer winner) {

    }

    @Override
    public void endBattle(BonanzaSpleefPlayer winner) {

    }

    @Override
    protected void sendStartMessage() {
        
    }
    
    @Override
    protected void setupBattleRequests() {
    
    }
    
    @Override
    protected void setupBaseSettings() {
        super.setupBaseSettings();
        SpleefUtils.setupBaseSettings(this);
    }
    
    @Override
    protected void setupScoreboard() {
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
    protected void addKnockout(BonanzaSpleefPlayer closest, BonanzaSpleefPlayer knockedOut) {
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
                || (battlers.get(bountyPlayer.getCorePlayer())).getKnockoutStreak() < closest.getKnockoutStreak()) {
            bountyPlayer = closest;
        }
    }

    @Override
    protected void onSpectatorEnter(CorePlayer cp) {
        addBattler(cp);
    }

    @Override
    protected void onGlobalSpectatorEnter(CorePlayer cp) {
        addBattler(cp);
    }

    /**
     * @param cp Battling Core Player
     */
    @Override
    protected void failBattler(CorePlayer cp) {
        BonanzaSpleefPlayer bp = battlers.get(cp);
        gameWorld.doFailBlast(cp);
        BonanzaSpleefPlayer closest = getClosestBattler(cp);
        if (closest != null) {
            addKnockout(closest, bp);
        }
        bp.respawn();
        leavePlayer(cp);
        if (bp.getEnteredState() == BattleState.SPECTATOR_GLOBAL) {
            addGlobalSpectator(cp);
        } else {
            addSpectator(cp, null);
        }
        cp.teleport(arena.getSpectatorSpawn());
        updateScoreboard();
    }
    
    @Override
    public void reset() {

    }
    
    @Override
    public void setPlayTo(int i) {

    }
    
}
