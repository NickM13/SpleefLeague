package com.spleefleague.core.player.collectible;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.database.annotation.DBField;

/**
 * @author NickM13
 * @since 4/19/2020
 */
public abstract class Holdable extends Collectible {
    
    public Holdable() {
        super();
    }

    /**
     * Called when a player clicks on this collectible on
     * their collections menu
     *
     * @param cp Core Player
     */
    @Override
    public void onEnable(CorePlayer cp) {
    
    }
    
    /**
     * Called when another collectible of the same type has
     * been enabled
     *
     * @param cp Core Player
     */
    @Override
    public void onDisable(CorePlayer cp) {
    
    }

    public abstract void onRightClick(CorePlayer cp);
    
}
