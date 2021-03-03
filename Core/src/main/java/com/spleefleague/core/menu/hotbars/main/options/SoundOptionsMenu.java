/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main.options;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.util.CoreUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class SoundOptionsMenu {

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
                    .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Sound Options")
                    .setDisplayItem(Material.MUSIC_DISC_13, 1)
                    .setDescription("Options for Sounds")
                    .createLinkedContainer("Sound Options");

            for (int i = 0; i < 5; i++) {
                menuItem.getLinkedChest().addDeadSpace(2, i);
            }

            menuItem.getLinkedChest()
                    .addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName("Gadget Sounds")
                            .setDescription("")
                            .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.MUSIC_DISC_13, 1))
                            .setCloseOnAction(false));
            menuItem.getLinkedChest().addMenuItem(
                    InventoryMenuAPI.createItemToggle()
                            .setAction(cp -> cp.getOptions().toggle("Sound:Gadget"))
                            .setEnabledFun(cp -> cp.getOptions().getBoolean("Sound:Gadget")));

            /*
            menuItem.getLinkedChest()
                    .addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName("Shovel Sounds")
                            .setDescription("")
                            .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.MUSIC_DISC_13, 1))
                            .setCloseOnAction(false));
            menuItem.getLinkedChest().addMenuItem(
                    InventoryMenuAPI.createItemToggle()
                            .setAction(cp -> cp.getOptions().toggle("Sound:Shovel"))
                            .setEnabledFun(cp -> cp.getOptions().getBoolean("Sound:Shovel")));

            menuItem.getLinkedChest()
                    .addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName("Overworld Music")
                            .setDescription("")
                            .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.MUSIC_DISC_13, 1))
                            .setCloseOnAction(false));
            menuItem.getLinkedChest().addMenuItem(
                    InventoryMenuAPI.createItemToggle()
                            .setAction(cp -> cp.getOptions().toggle("Sound:Player"))
                            .setEnabledFun(cp -> cp.getOptions().getBoolean("Sound:Player")));

            menuItem.getLinkedChest()
                    .addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setName("Battle Music")
                            .setDescription("")
                            .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.MUSIC_DISC_13, 1))
                            .setCloseOnAction(false));
            menuItem.getLinkedChest().addMenuItem(
                    InventoryMenuAPI.createItemToggle()
                            .setAction(cp -> cp.getOptions().toggle("Sound:Player"))
                            .setEnabledFun(cp -> cp.getOptions().getBoolean("Sound:Player")));
            */
        }
        return menuItem;
    }

}
