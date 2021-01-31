package com.spleefleague.core.menu.hotbars.main.collectible;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayerCollectibles;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.player.collectible.pet.Pet;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/19/2020
 */
public class PetMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = CorePlayerCollectibles.createCollectibleContainer(Pet.class,
                InventoryMenuAPI.createItemDynamic()
                        .setName("Pets")
                        .setDisplayItem(Material.BONE, 1)
                        .setDescription("Collection of Pets!"));
    }
    
    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) init();
        return menuItem;
    }
    
}
