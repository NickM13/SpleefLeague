package com.spleefleague.core.menu.menus.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/18/2020
 */
public class CollectiblesMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            menuItem.setName("Collectibles")
                    .setDescription("Collection of Collectibles!")
                    .setDisplayItem(Material.CHEST)
                    .createLinkedContainer("Collectibles Menu");
        }
        return menuItem;
    }
    
}
