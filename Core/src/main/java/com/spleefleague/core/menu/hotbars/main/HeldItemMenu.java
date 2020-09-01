/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.player.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class HeldItemMenu {
    
    protected static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItem()
                .setName(ChatColor.BLUE + "" + ChatColor.BOLD + "Held Item")
                .setDisplayItem(Material.BAKED_POTATO)
                .setDescription("Change your held item")
                .setAvailability(cp -> cp.getRank().hasPermission(Rank.DONOR_1))
                .createLinkedContainer("Held Item");
    
        menuItem.getLinkedChest()
                .setOpenAction((container, cp1) -> {
                    container.clearUnsorted();
                    
                    menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                            .setName("None")
                            .setDescription("")
                            .setDisplayItem(Material.BAKED_POTATO)
                            .setAction(cp -> cp.getCollectibles().setHeldItem(null))
                            .setCloseOnAction(false));
                    
                    for (Holdable holdable : cp1.getCollectibles().getAllHoldables()) {
                        container.addMenuItem(InventoryMenuAPI.createItem()
                                .setName(holdable.getName())
                                .setDescription(holdable.getDescription())
                                .setDisplayItem(holdable.getDisplayItem())
                                .setAction(cp2 -> cp2.getCollectibles().setHeldItem(holdable))
                                .setCloseOnAction(false));
                    }
                });
    
        menuItem.getLinkedChest()
                .addStaticItem(InventoryMenuAPI.createItem()
                        .setName("Held Item")
                        .setDescription(cp -> cp.getCollectibles().hasHeldItem()
                                ? cp.getCollectibles().getHeldItem().getDescription() : "")
                        .setDisplayItem(cp -> cp.getCollectibles().hasHeldItem()
                                ? cp.getCollectibles().getHeldItem().getDisplayItem() : new ItemStack(Material.BAKED_POTATO)),
                        4, 4);
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
