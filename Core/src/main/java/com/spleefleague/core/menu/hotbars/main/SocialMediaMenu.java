/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.socialmedia.StaffMenu;
import com.spleefleague.core.settings.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class SocialMediaMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Social Media")
                .setDisplayItem(Material.MELON_SLICE, 1)
                .setSelectedItem(Material.MELON_SLICE, 2)
                .setDescription("Check us out on MySpace!")
                .createLinkedContainer("Social Media");

        menuItem.getLinkedChest().setPageBoundaries(1, 3, 1, 7);

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Website")
                .setDisplayItem(Material.MELON_SLICE, 1)
                .setDescription(Settings.getDiscord().getUrl()), 6, 4);

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Online Store")
                .setDisplayItem(Material.EMERALD, 1)
                .setDescription(Settings.getDiscord().getUrl()), 5, 4);

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Discord")
                .setDisplayItem(Material.WRITABLE_BOOK, 4)
                .setDescription(Settings.getDiscord().getUrl()), 4, 4);

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Twitter")
                .setDisplayItem(Material.COOKED_CHICKEN, 3)
                .setDescription(Settings.getDiscord().getUrl()), 3, 4);

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "YouTube")
                .setDisplayItem(Material.REDSTONE, 2)
                .setDescription(Settings.getDiscord().getUrl()), 2, 4);

        menuItem.getLinkedChest().addStaticItem(StaffMenu.getItem(), 1, 4);
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
