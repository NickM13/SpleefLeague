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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class CreditsMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItem()
                .setName("Server Credits")
                .setDisplayItem(new ItemStack(Material.BOOK))
                .setDescription("View the Players that made SpleefLeague possible!")
                .createLinkedContainer("Server Credits");
    
        for (Credits credit : Credits.getCredits()) {
            CorePlayer cp = Core.getInstance().getPlayers().getOffline(credit.getUuid());
            menuItem.getLinkedContainer()
                    .addMenuItem(InventoryMenuAPI.createItem()
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
