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
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class OreMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("Ores")
                .setDisplayItem(Material.QUARTZ, 1)
                .setDescription("View the ores you have collected")
                .createLinkedContainer("Ores");

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GOLD + "Coins")
                .setDisplayItem(Material.GOLD_NUGGET)
                .setDescription(cp -> "You Have: " + cp.getPurse().getCurrency(CoreCurrency.COIN))
                .setCloseOnAction(false));

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GREEN + "Common Ores")
                .setDisplayItem(Material.COAL_ORE)
                .setDescription(cp -> "You Have: " + cp.getPurse().getCurrency(CoreCurrency.COIN))
                .setCloseOnAction(false));

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.AQUA + "Rare Ores")
                .setDisplayItem(Material.IRON_ORE)
                .setDescription(cp -> "You Have: " + cp.getPurse().getCurrency(CoreCurrency.COIN))
                .setCloseOnAction(false));

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.DARK_PURPLE + "Epic Ores")
                .setDisplayItem(Material.GOLD_ORE)
                .setDescription(cp -> "You Have: " + cp.getPurse().getCurrency(CoreCurrency.COIN))
                .setCloseOnAction(false));

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.YELLOW + "Legendary Ores")
                .setDisplayItem(Material.DIAMOND_ORE)
                .setDescription(cp -> "You Have: " + cp.getPurse().getCurrency(CoreCurrency.COIN))
                .setCloseOnAction(false));
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
