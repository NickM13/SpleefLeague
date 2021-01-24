/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.main.credits.Credits;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class CreditsMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Server Credits")
                .setDisplayItem(Material.MELON_SLICE, 1)
                .setSelectedItem(Material.MELON_SLICE, 2)
                .setDescription("View the Players that made SpleefLeague possible!")
                .createLinkedContainer("Server Credits");

        menuItem.getLinkedChest().setPageBoundaries(1, 3, 1, 7);
    
        for (Credits credit : Credits.getCredits()) {
            CorePlayer cp = Core.getInstance().getPlayers().getOffline(credit.getUuid());
            menuItem.getLinkedChest()
                    .addMenuItem(InventoryMenuAPI.createItemDynamic()
                                    .setDisplayItem(InventoryMenuUtils.createCustomSkull(credit.getUuid()))
                                    .setName(cp.getDisplayName())
                                    .setDescription(credit.getDescription())
                                    .setCloseOnAction(false),
                            credit.getSlot());
        }
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
