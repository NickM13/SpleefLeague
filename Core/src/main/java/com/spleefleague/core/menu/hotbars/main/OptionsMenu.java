/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.options.ChatOptionsMenu;
import com.spleefleague.core.menu.hotbars.main.options.StaffToolsMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class OptionsMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            // Options Menus
            menuItem = InventoryMenuAPI.createItemDynamic()
                    .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Options")
                    .setDisplayItem(Material.WRITABLE_BOOK, 1)
                    .setSelectedItem(Material.WRITABLE_BOOK, 2)
                    .setDescription("Customize your SpleefLeague experience")
                    .createLinkedContainer("Options");

            menuItem.getLinkedChest().addStaticItem(ChatOptionsMenu.getItem(), 6, 0);
            menuItem.getLinkedChest().addStaticItem(StaffToolsMenu.getItem(), 5, 0);
        }
        return menuItem;
    }

}
