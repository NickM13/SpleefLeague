/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class HatMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItem()
                .setName("Hats")
                .setDisplayItem(Material.LEATHER_HELMET)
                .setDescription("Pick a hat, any hat!")
                .setAction(cp -> { cp.sendMessage("Imagine this opened a menu with lots of pretty hats!"); });
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
