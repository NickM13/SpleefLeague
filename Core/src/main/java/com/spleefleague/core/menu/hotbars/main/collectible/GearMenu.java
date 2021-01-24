/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main.collectible;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayerCollectibles;
import com.spleefleague.core.player.collectible.hat.Hat;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class GearMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("Hats")
                .setDisplayItem(Material.LEATHER_HELMET)
                .setDescription("Pick a hat, any hat!")
                .createLinkedContainer("Hats");

        CorePlayerCollectibles.createCollectibleContainer(Hat.class, menuItem.getLinkedChest(), true);
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
