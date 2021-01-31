package com.spleefleague.core.player.collectible.hat;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.vendor.Vendorable;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/20/2020
 */
public class Hat extends Collectible {

    /**
     * Load hats from the SpleefLeague:Hats collection
     */
    public static void init() {
        Vendorable.registerParentType(Hat.class);
    
        InventoryMenuAPI.createItemHotbar(39, "Hat")
                .setName(cp -> cp.getCollectibles().getActiveName(Hat.class))
                .setDisplayItem(cp -> cp.getCollectibles().getActiveIcon(Hat.class))
                .setDescription(cp -> cp.getCollectibles().getActive(Hat.class).getDescription())
                .setAvailability(cp -> !cp.isBattler() && cp.getCollectibles().hasActive(Hat.class) && cp.getCollectibles().isEnabled(Hat.class));
    }

    public static void close() {
    
    }

    private static final Material DEFAULT_HAT_MAT = Material.BARRIER;
    
    public Hat() {
        super();
    }

    public Hat(String identifier, String name) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.material = DEFAULT_HAT_MAT;
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
    
    /**
     * Whether an item is available for purchasing for things
     * such as requiring prerequisites, levels or achievements
     *
     * @param cp Core Player
     * @return Availability
     */
    @Override
    public boolean isAvailableToPurchase(CorePlayer cp) {
        return false;
    }
}
