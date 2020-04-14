/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.banana;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.util.Dimension;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.game.SpleefBattleDynamic;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class BananaSpleefBattle extends SpleefBattleDynamic {
    
    protected static long FIELD_RESET = 30 * 1000L;
    protected long fieldResetTime = 0;
    
    protected List<Vector> possibleSpawns;
    
    public BananaSpleefBattle(List<DBPlayer> players, BananaSpleefArena arena) {
        super(players, arena);
        
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
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        chatGroup.setTeamDisplayName("PlayerCount", Chat.DEFAULT + "Players: " + Chat.SCORE + battlers.size());
        
        for (int i = 0; i < sortedBattlers.size() && i < seenScores; i++) {
            BattlePlayer bp = sortedBattlers.get(i);
            chatGroup.setTeamDisplayName("PLACE" + i, Chat.PLAYER_NAME + bp.player.getName() + ": " + Chat.SCORE + bp.points);
        }
        
        for (BattlePlayer bp : battlers.values()) {
            chatGroup.setTeamDisplayNamePersonal(bp.player, "PKnockout", Chat.DEFAULT + "Streak: " + Chat.SCORE + bp.knockoutStreak);
        }
    }
    
    protected float getResetPercent() {
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
            fillFieldNoReset();
        }
    }
    
    @Override
    protected void startCountdown() {
        countdown = 0;
        gameWorld.setEdittable(true);
    }
    
    @Override
    protected void fillField() {
        fillFieldFast();
    }
    @Override
    protected void sendStartMessage() {
        
    }
    
    @Override
    protected void startBattle() {
        chatGroup.addTeam("PlayerCount", Chat.SCORE + "Player Count");
        chatGroup.addTeam("PKnockout", Chat.SCORE + "");
        super.startBattle();
        for (BattlePlayer bp : battlers.values()) {
            bp.player.getPlayer().getInventory().addItem(bp.player.getActiveShovel().getItem());
        }
    }
    
    @Override
    protected void respawnPlayer(BattlePlayer bp) {
        Random r = new Random();
        Vector spawn = possibleSpawns.get(Math.abs(r.nextInt()) % possibleSpawns.size());
        Location spawnLoc = new Location(bp.player.getPlayer().getWorld(), spawn.getX(), spawn.getY() + 1, spawn.getZ());
        double theta = Math.atan2((double) (spawn.getZ() - arena.getCenter().z), (double) (spawn.getX() - arena.getCenter().x));
        spawnLoc.setYaw((float) Math.toDegrees(theta) + 90);
        bp.player.getPlayer().teleport(spawnLoc);
    }
    
    protected void addKnockout(BattlePlayer bp) {
        bp.knockouts++;
        bp.knockoutStreak++;
        switch (bp.knockoutStreak) {
            case 3:
                chatGroup.sendMessage(bp.player.getDisplayName() + " is on a Killing Spleef!");
                break;
            case 5:
                chatGroup.sendMessage(bp.player.getDisplayName() + " is Unspleefable!");
                break;
            case 7:
                chatGroup.sendMessage(bp.player.getDisplayName() + " is Smashing Spleefers!");
                break;
            case 10:
                chatGroup.sendMessage(bp.player.getDisplayName() + " is probably SaberTTiger!");
                break;
        }
    }
    
    @Override
    protected void failPlayer(SpleefPlayer sp) {
        for (BattlePlayer bp : battlers.values()) {
            if (bp.player.equals(sp)) {
                bp.knockoutStreak = 0;
                gameWorld.doFailBlast(sp.getPlayer());
                BattlePlayer cbp = getClosestPlayer(bp);
                if (cbp != null) {
                    addKnockout(cbp);
                    sortBattlers();
                }
                resetPlayer(bp);
                break;
            }
        }
        updateScoreboard();
    }
    
}
