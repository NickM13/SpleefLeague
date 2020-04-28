package com.spleefleague.core.menu.hotbars.main.collectible;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.collectible.Collectible;
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
        menuItem = InventoryMenuAPI.createItem()
                .setName("Pets")
                .setDescription("Collection of Pets!")
                .setDisplayItem(Material.FOX_SPAWN_EGG)
                .createLinkedContainer("Pets Menu");
        
        menuItem.getLinkedContainer()
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    for (Vendorable vendorable : Vendorables.getAll(Pet.class).values()) {
                        container.addMenuItem(InventoryMenuAPI.createItem()
                                .setName(vendorable.getName())
                                .setDisplayItem(vendorable.getDisplayItem())
                                .setDescription(vendorable.getDescription())
                                .setAction(cp2 -> {
                                    cp2.getCollectibles().setActiveItem((Collectible) vendorable);
                                }));
                    }
                });
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
