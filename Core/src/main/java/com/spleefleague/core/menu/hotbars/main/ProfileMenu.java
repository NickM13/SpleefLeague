/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.profile.*;
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
                    .setDescription("View your SpleefLeague profile")
                    .createLinkedContainer("Profile");

            //menuItem.getLinkedChest().addStaticItem(PartyMenu.getItem(), 3, 4);
            //menuItem.getLinkedChest().addStaticItem(AchievementsMenu.getItem(), 2, 4);
            //menuItem.getLinkedChest().addStaticItem(QuestsMenu.getItem(), 3, 4);
            //menuItem.getLinkedChest().addStaticItem(GoldenLeavesMenu.getItem(), 5, 4);
            menuItem.getLinkedChest().addStaticItem(OresMenu.getItem(), 6, 4);
            //menuItem.getLinkedChest().addStaticItem(StatisticsMenu.getItem(), 6, 4);
        }
        return menuItem;
    }

}
