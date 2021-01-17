package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.collectible.HatMenu;
import com.spleefleague.core.menu.hotbars.main.collectible.PetMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/18/2020
 */
public class GamemodeMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItem()
                .setName(ChatColor.BLUE + "" + ChatColor.BOLD + "Gamemodes")
                .setDescription("Check these out!")
                .setDisplayItem(Material.COMPASS, 2)
                .createLinkedContainer("Gamemodes");
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
