package com.spleefleague.core.game.battle.solo;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;

import java.util.List;

/**
 * @author NickM13
 * @since 4/24/2020
 */
public abstract class SoloBattle<A extends Arena, BP extends BattlePlayer> extends Battle<A, BP> {
    
    public SoloBattle(CorePlugin<?> plugin, List<CorePlayer> players, A arena, Class<BP> battlePlayerClass) {
        super(plugin, players, arena, battlePlayerClass);
    }
    
    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
    
    }
    
    /**
     * Initialize the players, called in startBattle()
     */
    @Override
    protected void setupBattlers() {
    
    }
    
    /**
     * Send a message on the start of a battle
     */
    @Override
    protected void sendStartMessage() {
    
    }
    
    @Override
    protected void fillField() {
    
    }
    
    /**
     * Called when a battler joins mid-game (if available)
     *
     * @param cp
     */
    @Override
    protected final void joinBattler(CorePlayer cp) {
    
    }
    
    /**
     * Save the battlers stats
     * Called when a battler is removed from the battle
     *
     * @param cp
     */
    @Override
    protected void saveBattlerStats(CorePlayer cp) {
    
    }
    
    /**
     * Called when a battler leaves boundaries
     *
     * @param cp CorePlayer
     */
    @Override
    protected void failBattler(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player surrenders (/ff, /leave)
     *
     * @param cp CorePlayer
     */
    @Override
    public void surrender(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player requests the game to end (/endgame)
     *
     * @param cp CorePlayer
     */
    @Override
    public void requestEndGame(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player requests to pause the game with specified
     * time (/pause <seconds>)
     *
     * @param cp      CorePlayer
     * @param timeout Seconds
     */
    @Override
    public void requestPause(CorePlayer cp, int timeout) {
    
    }
    
    /**
     * Called when a player requests to pause the game (/pause)
     *
     * @param cp CorePlayer
     */
    @Override
    public void requestPause(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player requests to reset the field (/reset)
     *
     * @param cp CorePlayer
     */
    @Override
    public void requestReset(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player requests to change the
     * PlayTo score (/playto)
     *
     * @param cp CorePlayer
     */
    @Override
    public void requestPlayTo(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player requests to change the
     * PlayTo score with specified score (/playto <score>)
     *
     * @param cp     CorePlayer
     * @param playTo
     */
    @Override
    public void requestPlayTo(CorePlayer cp, int playTo) {
    
    }
    
    /**
     * Called when a battler wants to leave (/leave, /ff)
     *
     * @param cp Battler CorePlayer
     */
    @Override
    protected void leaveBattler(CorePlayer cp) {
    
    }
    
    /**
     * Called every 1 second or on score updates
     * Updates the player scoreboards
     */
    @Override
    public void updateScoreboard() {
    
    }
    
    /**
     * Called every 1/10 second
     * Updates the field on occasion for events such as
     * auto-regenerating maps
     */
    @Override
    public void updateField() {
    
    }
    
    /**
     * Updates the experience bar of players in the game
     */
    @Override
    public void updateExperience() {
    
    }
}
