package com.spleefleague.core.menu.hotbars.main.profile;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import org.bukkit.Material;

public class PurseMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("Purse")
                .setDisplayItem(Material.GOLD_NUGGET, 1)
                .setDescription("View your purse")
                .createLinkedContainer("Purse");
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