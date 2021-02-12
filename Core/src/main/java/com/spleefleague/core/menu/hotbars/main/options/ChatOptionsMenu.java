/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main.options;

import com.google.common.collect.Lists;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.util.CoreUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class ChatOptionsMenu {

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
                    .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Chat Options")
                    .setDisplayItem(Material.BIRCH_SIGN, 1)
                    .setDescription("Options for Chat")
                    .createLinkedContainer("Chat Options");

            for (int i = 0; i < 5; i++) {
                menuItem.getLinkedChest().addDeadSpace(2, i);
            }

            for (String name : CoreUtils.sortCollectionByName(CoreUtils.enumToStrSet(ChatChannel.class, false))) {
                ChatChannel channel = ChatChannel.valueOf(name);
                menuItem.getLinkedChest()
                        .addMenuItem(InventoryMenuAPI.createItemDynamic()
                                .setName(channel.getName())
                                .setDescription("")
                                .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.WRITABLE_BOOK, 3))
                                .setCloseOnAction(false)
                                .setVisibility(channel::isAvailable));
                menuItem.getLinkedChest().addMenuItem(
                        InventoryMenuAPI.createItemToggle()
                                .setAction(cp -> cp.getOptions().toggle("Chat:" + channel.name()))
                                .setEnabledFun(cp -> cp.getOptions().getBoolean("Chat:" + channel.name()))
                                .setVisibility(channel::isAvailable));
            }
        }
        return menuItem;
    }

}
