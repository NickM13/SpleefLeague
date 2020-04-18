package com.spleefleague.core.player.collectible;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.vendor.Vendorable;

/**
 * Collectible items are vendorable items that when
 * purchased (or are default available) are added to a
 * collectibles menu where you can activate/deactivate
 * them for different effects
 *
 * @author NickM13
 * @since 4/18/2020
 */
public abstract class Collectible extends Vendorable {
    
    public Collectible(String type) {
        super(type);
    }
    
    /**
     * Called when a player clicks on this collectible on
     * their collections menu
     */
    public abstract void onEnable();
    
    /**
     * Called when another collectible of the same type has
     * been enabled
     */
    public abstract void onDisable();
    
    /**
     * Whether an item is available for purchasing for things
     * such as requiring prerequisites, levels or achievements
     *
     * @param cp
     * @return Availability
     */
    @Override
    public abstract boolean isAvailable(CorePlayer cp);
    
    /**
     * Called when a player successfully purchases this item from the vendor
     *
     * @param cp
     */
    @Override
    public void purchase(CorePlayer cp) {
    
    }
    
}
