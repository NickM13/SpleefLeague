package com.spleefleague.core.game;

import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.entity.Player;

/**
 * BattlePlayer stores a DBPlayer and fallen state
 * of a player, managed in Battle's battlers
 * 
 * @author NickM
 * @since 4/14/2020
 */
public abstract class BattlePlayer {

    private CorePlayer cp;
    private Battle<?> battle;
    private boolean fallen;

    public BattlePlayer(CorePlayer cp, Battle<?> battle) {
        this.cp = cp;
        this.battle = battle;
        init();
    }

    public void init() {
        this.fallen = false;
    }

    /**
     * @return Core Player
     */
    public CorePlayer getCorePlayer() {
        return cp;
    }

    /**
     * Simplifies getDBPlayer().getPlayer()
     *
     * @return Player
     */
    public Player getPlayer() {
        return cp.getPlayer();
    }

    /**
     * @return Battle
     */
    public Battle<?> getBattle() {
        return battle;
    }

    /**
     * Called when a player spawns into a battle
     */
    public void respawn() {
        fallen = false;
    }

    /**
     * @return Fallen State
     */
    public boolean isFallen() {
        return fallen;
    }

    /**
     * @param state Fallen State
     */
    public void setFallen(boolean state) {
        fallen = state;
    }

}
