/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.rank.Rank;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * @author NickM13
 */
public class ModeratorMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            menuItem = InventoryMenuAPI.createItem()
                    .setName("Moderative Menu")
                    .setDisplayItem(Material.REDSTONE)
                    .setDescription("Useful tools to deal with the general population")
                    .setMinRank(Rank.MODERATOR)
                    .createLinkedContainer("Moderative Menu");
            
            menuItem.getLinkedContainer()
                    .addMenuItem(InventoryMenuAPI.createItem()
                            .setName("Clear Effects")
                            .setDisplayItem(Material.MILK_BUCKET)
                            .setAction(cp -> {
                                    for (PotionEffect pe : cp.getPlayer().getActivePotionEffects()) {
                                        cp.getPlayer().removePotionEffect(pe.getType());
                                    }
                                    })
                            .setCloseOnAction(false));
            
            menuItem.getLinkedContainer()
                    .addMenuItem(InventoryMenuAPI.createItem()
                            .setName("Night Vision")
                            .setDisplayItem(InventoryMenuUtils.createCustomPotion(PotionType.NIGHT_VISION))
                            .setAction(cp -> cp.getPlayer().addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(Integer.MAX_VALUE, 0)))
                            .setCloseOnAction(false));
            
            menuItem.getLinkedContainer()
                    .addMenuItem(InventoryMenuAPI.createItem()
                            .setName("Command Block")
                            .setDisplayItem(Material.COMMAND_BLOCK)
                            .setAction(cp -> cp.getPlayer().getInventory().addItem(new ItemStack(Material.COMMAND_BLOCK)))
                            .setCloseOnAction(false));
            
            menuItem.getLinkedContainer()
                    .addMenuItem(InventoryMenuAPI.createItem()
                            .setName("Repeating Command Block")
                            .setDisplayItem(Material.REPEATING_COMMAND_BLOCK)
                            .setAction(cp -> cp.getPlayer().getInventory().addItem(new ItemStack(Material.REPEATING_COMMAND_BLOCK)))
                            .setCloseOnAction(false));
            
            menuItem.getLinkedContainer()
                    .addMenuItem(InventoryMenuAPI.createItem()
                            .setName("Barrier")
                            .setDisplayItem(Material.BARRIER)
                            .setAction(cp -> cp.getPlayer().getInventory().addItem(new ItemStack(Material.BARRIER)))
                            .setCloseOnAction(false));
        }
        return menuItem;
    }

}
