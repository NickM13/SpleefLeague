package com.spleefleague.core.player.collectible;

import com.spleefleague.core.player.CorePlayer;

/**
 * @author NickM13
 * @since 4/19/2020
 */
public abstract class Holdable extends Collectible {
    
    // Show all unlocked in the Holdables menu or just the current active Holdable item
    protected final boolean showAll;
    
    public Holdable(boolean showAll) {
        super();
        this.showAll = showAll;
    }
    
    public boolean isShowAll() {
        return showAll;
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
