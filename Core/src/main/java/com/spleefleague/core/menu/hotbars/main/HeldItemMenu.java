/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.*;
import com.spleefleague.core.menu.hotbars.main.collectible.GearMenu;
import com.spleefleague.core.menu.hotbars.main.collectible.HatMenu;
import com.spleefleague.core.player.CorePlayerCollectibles;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.player.collectible.gear.Gear;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.vendor.Vendorable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class HeldItemMenu {
    
    protected static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.BLUE + "" + ChatColor.BOLD + "Held Item")
                .setDisplayItem(Material.DIAMOND_CHESTPLATE, 1)
                .setSelectedItem(Material.DIAMOND_CHESTPLATE, 2)
                .setDescription("Change your held item")
                .setAvailability(cp -> cp.getRank().hasPermission(Rank.DONOR_1))
                .createLinkedContainer("Held Item");

        InventoryMenuContainerChest container = menuItem.getLinkedChest();

        container.addStaticItem(GearMenu.getItem(), 5, 2);
        container.addStaticItem(CorePlayerCollectibles.createActiveMenuItem(Gear.class), 5, 3);
        container.addStaticItem(CorePlayerCollectibles.createToggleMenuItem(Gear.class), 5, 4);

        container.addStaticItem(HatMenu.getItem(), 3, 2);
        container.addStaticItem(CorePlayerCollectibles.createActiveMenuItem(Hat.class), 3, 3);
        container.addStaticItem(CorePlayerCollectibles.createToggleMenuItem(Hat.class), 3, 4);
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
