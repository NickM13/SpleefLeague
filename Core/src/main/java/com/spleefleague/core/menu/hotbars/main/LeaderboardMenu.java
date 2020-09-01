/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;

import com.spleefleague.core.menu.InventoryMenuUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class LeaderboardMenu {
    
    protected static InventoryMenuItem menuItem = null;
    
    /**
     * Gets the menu item for this menu, if it doesn't exist already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            // Options Menus
            menuItem = InventoryMenuAPI.createItem()
                    .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Leaderboards")
                    .setDisplayItem(new ItemStack(Material.OAK_SIGN))
                    .setDescription("View the Top Players of SpleefLeague!")
                    .createLinkedContainer("Leaderboards");
            
            for (int i = 0; i < menuItem.getLinkedChest().getPageItemTotal() / 2; i++) {
                menuItem.getLinkedChest().addStaticItem(InventoryMenuUtils.createLockedMenuItem("Other"), i * 2 + 9);
            }
        }
        return menuItem;
    }

}
