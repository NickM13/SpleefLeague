/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.*;
import com.spleefleague.core.menu.hotbars.main.collectible.GearMenu;
import com.spleefleague.core.menu.hotbars.main.collectible.HatMenu;
import com.spleefleague.core.menu.hotbars.main.collectible.PetMenu;
import com.spleefleague.core.player.CorePlayerCollectibles;
import com.spleefleague.core.player.collectible.gear.Gear;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.player.collectible.pet.Pet;
import com.spleefleague.core.player.rank.CoreRank;
import org.bukkit.ChatColor;
import org.bukkit.Material;

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
                .setAvailability(cp -> cp.getRank().hasPermission(CoreRank.DONOR_1))
                .createLinkedContainer("Held Item");

        InventoryMenuContainerChest container = menuItem.getLinkedChest();

        container.addMenuItem(HatMenu.getItem(), 1, 0);
        container.addMenuItem(CorePlayerCollectibles.createActiveMenuItem(Hat.class), 1, 1);
        container.addMenuItem(CorePlayerCollectibles.createToggleMenuItem(Hat.class), 1, 2);

        container.addMenuItem(GearMenu.getItem(), 2, 0);
        container.addMenuItem(CorePlayerCollectibles.createActiveMenuItem(Gear.class), 2, 1);
        container.addMenuItem(CorePlayerCollectibles.createToggleMenuItem(Gear.class), 2, 2);

        container.addMenuItem(PetMenu.getItem(), 4, 0);
        container.addMenuItem(CorePlayerCollectibles.createActiveMenuItem(Pet.class), 4, 1);
        container.addMenuItem(CorePlayerCollectibles.createToggleMenuItem(Pet.class), 4, 2);
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
