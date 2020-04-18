package com.spleefleague.core.menu.menus;

import com.spleefleague.core.menu.InventoryMenuItem;

/**
 * @author NickM13
 * @since 4/18/2020
 */
public class MainMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
        
        }
        return menuItem;
    }
    
}
