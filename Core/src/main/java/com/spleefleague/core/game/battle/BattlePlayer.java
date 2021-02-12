package com.spleefleague.core.game.battle;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * BattlePlayer stores a DBPlayer and fallen state
 * of a player, managed in Battle's battlers
 *
 * @author NickM
 * @since 4/14/2020
 */
public abstract class BattlePlayer {

    private final CorePlayer cp;
    private final Player player;
    private final Battle<?> battle;
    private Location spawn;
    private int checkpoint;
    private boolean fallen;
    private int roundWins;
    private long lastWin = 0;
    private boolean channeling = false;

    public BattlePlayer(CorePlayer cp, Battle<?> battle) {
        this.cp = cp;
        this.player = cp.getPlayer();
        this.battle = battle;
        this.checkpoint = -1;
        this.roundWins = 0;
        init();
    }

    public void init() {
        this.fallen = false;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setCheckpoint(int checkpoint) {
        this.checkpoint = checkpoint;
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
            cp.getPlayer().playSound(cp.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 0.529732f);
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                cp.getPlayer().playSound(cp.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 0.707107f);
                cp.getPlayer().playSound(cp.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 0.890899f);
            }, 3L);
        });
    }

    public int getCheckpoint() {
        return checkpoint;
    }

    /**
     * @return Core Player
     */
    public CorePlayer getCorePlayer() {
        return cp;
    }

    /**
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return Battle
     */
    public Battle<?> getBattle() {
        return battle;
    }

    public void setChanneling(boolean state) {
        channeling = state;
    }

    public boolean isChanneling() {
        return channeling;
    }

    /**
     * Called when a player spawns into a battle
     */
    public void respawn() {
        channeling = false;
        fallen = false;
        if (checkpoint != -1) {
            player.teleport(battle.getCheckpoint(checkpoint));
        } else if (spawn != null) {
            player.teleport(spawn);
        }
    }

    public void onRightClick() {

    }

    public void onPlayerPunch(BattlePlayer target) {

    }

    public void onPlayerHit(BattlePlayer target) {

    }

    public void onBlockBreak() {

    }

    public void onSwapItem() {

    }

    public void onSlotChange(int newSlot) {

    }

    public void onDropItem() {

    }

    public void onStartSneak() {

    }

    public void onStopSneak() {

    }

    /**
     * @return Fallen State
     */
    public boolean isFallen() {
        return fallen;
    }

    public void addRoundWin() {
        roundWins++;
        lastWin = System.currentTimeMillis();
    }

    public int getRoundWins() {
        return roundWins;
    }

    public long getLastWin() {
        return lastWin;
    }

    /**
     * @param state Fallen State
     */
    public void setFallen(boolean state) {
        fallen = state;
    }

}
