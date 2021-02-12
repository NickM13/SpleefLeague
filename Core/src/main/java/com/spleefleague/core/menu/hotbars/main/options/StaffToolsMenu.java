/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main.options;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.main.options.moderator.ArenaMenu;
import com.spleefleague.core.player.rank.CoreRank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * @author NickM13
 */
public class StaffToolsMenu {

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
                    .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Staff Tools")
                    .setDisplayItem(Material.REDSTONE, 1)
                    .setDescription("A variety of tools useful for the maintenance and quality of SpleefLeague.")
                    .setAvailability(cp -> cp.getRank().hasPermission(CoreRank.TEMP_MOD))
                    .createLinkedContainer("Staff Tools");

            menuItem.getLinkedChest()
                    .addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName("Clear Effects")
                            .setDisplayItem(Material.MILK_BUCKET)
                            .setAction(cp -> {
                                for (PotionEffect pe : cp.getPlayer().getActivePotionEffects()) {
                                    cp.getPlayer().removePotionEffect(pe.getType());
                                }
                            })
                            .setCloseOnAction(false), 0, 3);

            menuItem.getLinkedChest()
                    .addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName("Night Vision")
                            .setDisplayItem(InventoryMenuUtils.createCustomPotion(PotionType.NIGHT_VISION))
                            .setAction(cp -> cp.getPlayer().addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(Integer.MAX_VALUE, 0)))
                            .setCloseOnAction(false), 1, 3);

            menuItem.getLinkedChest()
                    .addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName("Command Block")
                            .setDisplayItem(Material.COMMAND_BLOCK)
                            .setAction(cp -> cp.getPlayer().getInventory().addItem(new ItemStack(Material.COMMAND_BLOCK)))
                            .setCloseOnAction(false), 2, 3);

            menuItem.getLinkedChest()
                    .addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName("Barrier")
                            .setDisplayItem(Material.BARRIER)
                            .setAction(cp -> cp.getPlayer().getInventory().addItem(new ItemStack(Material.BARRIER)))
                            .setCloseOnAction(false), 3, 3);

            menuItem.getLinkedChest()
                    .addMenuItem(ArenaMenu.getItem());
        }
        return menuItem;
    }

}
