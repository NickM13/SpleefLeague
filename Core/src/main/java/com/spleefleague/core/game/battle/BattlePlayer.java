package com.spleefleague.core.game.battle;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Location;
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
    private final Battle<?, ?> battle;
    private Location spawn;
    private boolean fallen;
    private int roundWins;

    public BattlePlayer(CorePlayer cp, Battle<?, ?> battle) {
        this.cp = cp;
        this.battle = battle;
        this.roundWins = 0;
        init();
    }

    public void init() {
        this.fallen = false;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
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
        return cp.getPlayer();
    }

    /**
     * @return Battle
     */
    public Battle<?, ?> getBattle() {
        return battle;
    }

    /**
     * Called when a player spawns into a battle
     */
    public void respawn() {
        fallen = false;
        if (spawn != null) {
            getPlayer().teleport(spawn);
        }
    }

    /**
     * @return Fallen State
     */
    public boolean isFallen() {
        return fallen;
    }

    public void addRoundWin() {
        roundWins++;
    }
    
    public int getRoundWins() {
        return roundWins;
    }
    
    /**
     * @param state Fallen State
     */
    public void setFallen(boolean state) {
        fallen = state;
    }

}
