package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/18/2020
 */
public class ParticlesMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.BLUE + "" + ChatColor.BOLD + "Particles")
                .setDescription("Im not sure what this is D:")
                .setDisplayItem(Material.WHEAT_SEEDS, 1)
                .setSelectedItem(Material.WHEAT_SEEDS, 2)
                .createLinkedContainer("Particles");
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
