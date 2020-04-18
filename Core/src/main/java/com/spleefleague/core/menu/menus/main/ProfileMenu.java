/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.menus.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;

/**
 * @author NickM13
 */
public class ProfileMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            menuItem = InventoryMenuAPI.createItem()
                    .setName("Profile")
                    .setDisplayItem(cp -> InventoryMenuAPI.createCustomSkull(cp.getName()))
                    .setDescription("View statistics on your player character")
                    .createLinkedContainer("Profile");
        }
        return menuItem;
    }

}
