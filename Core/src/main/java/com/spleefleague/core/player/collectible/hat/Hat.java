package com.spleefleague.core.player.collectible.hat;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.pet.Pet;
import com.spleefleague.core.vendor.Vendorable;
import org.bson.Document;

/**
 * @author NickM13
 * @since 4/20/2020
 */
public class Hat extends Collectible {
    
    private static MongoCollection<Document> hatCol;
    
    /**
     * Load hats from the SpleefLeague:Hats collection
     */
    public static void init() {
        Vendorable.registerVendorableType(Hat.class);
        
        hatCol = Core.getInstance().getPluginDB().getCollection("Hats");
        
        for (Document doc : hatCol.find()) {
            Hat hat = new Hat();
            hat.load(doc);
        }
    
        InventoryMenuAPI.createItemHotbar(39, "Hat")
                .setName(cp -> cp.getCollectibles().getActive(Hat.class).getName())
                .setDisplayItem(cp -> cp.getCollectibles().getActive(Hat.class).getDisplayItem())
                .setDescription(cp -> cp.getCollectibles().getActive(Hat.class).getDescription())
                .setAvailability(cp -> !cp.isInBattle() && cp.getCollectibles().hasActive(Hat.class));
    }
    
    /**
     * Not implemented yet
     */
    public static void close() {
    
    }
    
    public Hat() {
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
