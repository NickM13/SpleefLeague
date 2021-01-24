/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.profile.FriendsMenu;
import com.spleefleague.core.menu.hotbars.main.profile.PurseMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;

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
            menuItem = InventoryMenuAPI.createItemDynamic()
                    .setName(ChatColor.BLUE + "" + ChatColor.BOLD + "Profile")
                    .setDisplayItem(Material.ARMOR_STAND, 1)
                    .setSelectedItem(Material.ARMOR_STAND, 2)
                    .setDescription("View statistics on your player character")
                    .createLinkedContainer("Profile");

            menuItem.getLinkedChest().addMenuItem(FriendsMenu.getItem(), 1, 1);
            menuItem.getLinkedChest().addMenuItem(PurseMenu.getItem(), 4, 2);
        }
        return menuItem;
    }

}
