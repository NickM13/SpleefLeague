/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.spleef.SpleefBattlePlayer;
import com.spleefleague.spleef.player.SpleefPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author NickM13
 */
public abstract class SpleefBattle extends Battle<SpleefArena> {
    
    /*
    SpleefBattle object contains a number of players, their scores,
    the spectators, an arena, and the game loop functions
    
    This looked like a good place to put a comment about how much
    I enjoy playing Spleef on the server SpleefLeague!
    */
    
    // Round countdown timer
    /*
    protected final Request<Integer> pauseRequest = new Request<>();
    protected final Request<Long> resetRequest = new Request<>();
    protected final Request<Integer> playToRequest = new Request<>();
    protected final Request endGameRequest = new Request();
    */
    
    protected static final Material SPLEEFER_TOOL = Material.DIAMOND_SHOVEL;
    
    // Needed points to win game
    protected static final int WIN_POINTS = 5;
    protected int playToPoints = WIN_POINTS;
    
    protected static final int MAX_SEEN_SCORES = 5;
    protected int seenScores = 0;

    public SpleefBattle(List<CorePlayer> players,
                        SpleefArena arena,
                        Class<? extends BattlePlayer> battlePlayerClass) {
        super(Spleef.getInstance(), players, arena, battlePlayerClass);
    }

    protected void startRound() {
    }

    protected void sortBattlers() {
        sortedBattlers.clear();
        for (BattlePlayer bp : battlers.values()) {
            SpleefBattlePlayer sbp = (SpleefBattlePlayer) bp;
            boolean inserted = false;
            int i = 0;
            for (BattlePlayer bp2 : sortedBattlers) {
                SpleefBattlePlayer sbp2 = (SpleefBattlePlayer) bp2;
                if (sbp.getPoints() > sbp2.getPoints()) {
                    sortedBattlers.add(i, sbp);
                    inserted = true;
                    break;
                }
                i++;
            }
            if (!inserted) {
                sortedBattlers.add(sbp);
            }
        }
    }
    
    @Override
    public abstract void updateScoreboard();

    @Override
    protected void setupBattleInventory(CorePlayer cp) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        cp.getPlayer().getInventory().addItem(sp.getActiveShovel().getItem());
    }
    
    protected void setupBattlers() {
        seenScores = 0;
        for (int i = 0; i < sortedBattlers.size(); i++) {
            if (i < MAX_SEEN_SCORES) {
                chatGroup.addTeam("PLACE" + seenScores, "Placeholder");
                seenScores++;
            }
        }
    }

    protected String getFormattedPlayerNames(Set<CorePlayer> players) {
        String formatted = "";
        for (CorePlayer cp : players) {
            formatted = formatted.concat(formatted.isEmpty() ? "" : ", " + cp.getDisplayName());
        }
        return formatted;
    }
    
    @Override
    protected void sendStartMessage() {
        Core.getInstance().sendMessage("A " +
                Chat.GAMEMODE + getMode().getDisplayName() + " " +
                Chat.DEFAULT + "match between " +
                getFormattedPlayerNames(players) +
                Chat.DEFAULT + " has begun on " +
                Chat.GAMEMAP + arena.getDisplayName());
    }

    @Override
    protected void setupBaseSettings() {
        gameWorld.addBreakTool(SPLEEFER_TOOL);
        gameWorld.addBreakableBlock(Material.SNOW_BLOCK);
    }

    @Override
    protected void saveBattlerStats(CorePlayer cp) {

    }

    /**
     * Applies elo change to all players in the battle
     * ELO change is 20 if players are the same rank
     * exponentially increasing/decreasing between (5, 40)
     */
    protected abstract void applyEloChange(BattlePlayer winner);

    /**
     * Remove layers from snow
     * @param pos BlockPosition
     * @param amount Layers
     */
    public void chipBlock(BlockPosition pos, int amount) {
        gameWorld.chipBlock(pos, amount);
    }
    
    protected void fillFieldFast() {
        // Clear field
        gameWorld.clear();
        for (Dimension field : arena.getField().getAreas()) {
            // Fill field with delayed blocks (snow goes up in stacks)
            for (int x = (int)field.getLow().x; x <= (int)field.getHigh().x; x++) {
                for (int y = (int)field.getLow().y; y <= (int)field.getHigh().y; y++) {
                    for (int z = (int)field.getLow().z; z <= (int)field.getHigh().z; z++) {
                        gameWorld.setBlock(new BlockPosition(x, y, z), Material.SNOW_BLOCK.createBlockData());
                    }
                }
            }
        }
    }
    
    protected void fillFieldFancy() {
        // Clear field
        gameWorld.clear();
        for (Dimension field : arena.getField().getAreas()) {
            // Fill field with delayed blocks (snow goes up in stacks)
            for (int x = (int)field.getLow().x; x <= (int)field.getHigh().x; x++) {
                for (int y = (int)field.getLow().y; y <= (int)field.getHigh().y; y++) {
                    for (int z = (int)field.getLow().z; z <= (int)field.getHigh().z; z++) {
                        gameWorld.setBlockDelayed(new BlockPosition(x, y, z), Material.SNOW_BLOCK.createBlockData(), 0.2D, arena.getSpawns());
                    }
                }
            }
        }
    }

    @Override
    protected void fillField() {
        fillFieldFancy();
    }

    public void resetRound() {
        startRound();
        Core.getInstance().sendMessage("The field has been reset");
    }

    protected abstract void endRound(BattlePlayer winner);

    protected abstract void endBattle(BattlePlayer winner);

    @Override
    public void requestPause(CorePlayer cp) {
        Core.getInstance().sendMessage("Under construction!");
    }
    @Override
    public void requestPause(CorePlayer cp, int timeout) {
        Core.getInstance().sendMessage("Under construction!");
    }
    
    @Override
    public void requestReset(CorePlayer cp) {
        Core.getInstance().sendMessage("Under construction!");
    }
    
    @Override
    public void requestPlayTo(CorePlayer cp) {
        Core.getInstance().sendMessage("Under construction!");
    }
    @Override
    public void requestPlayTo(CorePlayer cp, int playTo) {
        Core.getInstance().sendMessage("Under construction!");
    }
    
    @Override
    public void requestEndGame(CorePlayer cp) {
        Core.getInstance().sendMessage("Under construction!");
    }

    @Override
    protected void leaveBattler(CorePlayer cp) {

    }

    public void setPlayToPoints(int playToPoints) {
        this.playToPoints = playToPoints;
        Core.getInstance().sendMessage("The match is now set to playto " + Chat.GAMEMODE + playToPoints + Chat.DEFAULT);
    }
    
    protected BattlePlayer getClosestPlayer(BattlePlayer bp) {
        BattlePlayer closest = null;
        double closeDist = 0, dist;
        for (BattlePlayer bp2 : battlers.values()) {
            if (bp != bp2 && !bp2.isFallen()) {
                dist = bp.getPlayer().getPlayer().getLocation().distance(bp2.getPlayer().getPlayer().getLocation());
                if (closest == null || dist < closeDist) {
                    closest = bp2;
                    closeDist = dist;
                }
            }
        }
        return closest;
    }

    /**
     * @param closest Closest player
     * @param knockedOut Knocked out player
     */
    protected void addKnockout(SpleefBattlePlayer closest, SpleefBattlePlayer knockedOut) {
        closest.addKnockouts(1);
    }
    
    @Override
    protected void failBattler(CorePlayer cp) {
        for (BattlePlayer bp : battlers.values()) {
            if (bp.getCorePlayer().equals(cp)) {
                if (bp.isFallen()) return;
                bp.setFallen(true);
                remainingPlayers--;
                gameWorld.doFailBlast(cp);
                if (remainingPlayers > 0) {
                    SpleefBattlePlayer cbp = (SpleefBattlePlayer) getClosestPlayer(bp);
                    if (cbp != null) {
                        addKnockout(cbp, (SpleefBattlePlayer) bp);
                        sortBattlers();
                    }
                    if (remainingPlayers > 1) {
                        bp.getPlayer().setGameMode(GameMode.SPECTATOR);
                        if (cbp != null) {
                            gameWorld.setSpectator(bp.getCorePlayer(), cp);
                        }
                    }
                }
                break;
            }
        }
        if (remainingPlayers <= 1) {
            if (remainingPlayers < 1) {
                endRound(sortedBattlers.get(0));
            } else {
                for (BattlePlayer sbp : battlers.values()) {
                    if (!sbp.isFallen()) {
                        endRound(sbp);
                        break;
                    }
                }
            }
            startRound();
        }
        updateScoreboard();
    }
    
    @Override
    public void doCountdown() {
        super.doCountdown();
    }
    
}
