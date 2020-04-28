package com.spleefleague.core.player.collectible;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.player.collectible.key.Key;
import com.spleefleague.core.player.collectible.pet.Pet;
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
    
    public static void init() {
        Hat.init();
        Key.init();
        Pet.init();
    }
    
    public static void close() {
        Hat.close();
        Key.close();
        Pet.close();
    }
    
    public Collectible() {
        super();
    }
    
    /**
     * Called when a player clicks on this collectible on
     * their collections menu
     *
     * @param cp Core Player
     */
    public abstract void onEnable(CorePlayer cp);
    
    /**
     * Called when another collectible of the same type has
     * been enabled
     *
     * @param cp Core Player
     */
    public abstract void onDisable(CorePlayer cp);
    
    /**
     * Whether an item is available for purchasing for things
     * such as requiring prerequisites, levels or achievements
     *
     * @param cp
     * @return Availability
     */
    @Override
    public abstract boolean isAvailableToPurchase(CorePlayer cp);
    
    /**
     * Called when a player successfully purchases this item from the vendor
     *
     * @param cp
     */
    @Override
    public void purchase(CorePlayer cp) {
    
    }
    
}
