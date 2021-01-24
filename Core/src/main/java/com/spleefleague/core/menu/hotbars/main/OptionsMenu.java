/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class OptionsMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            // Options Menus
            menuItem = InventoryMenuAPI.createItemDynamic()
                    .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Options")
                    .setDisplayItem(Material.WRITABLE_BOOK, 1)
                    .setDescription("Customize your SpleefLeague experience")
                    .createLinkedContainer("Options Menu");

            // Chat Options Menus
            InventoryMenuItem chatOptionsItem = InventoryMenuAPI.createItemDynamic()
                    .setName("Chat Channels")
                    .setDisplayItem(new ItemStack(Material.WRITABLE_BOOK))
                    .setDescription("Toggle Chat Channels")
                    .createLinkedContainer("Chat Channels");

            for (ChatChannel.Channel channel : ChatChannel.Channel.values()) {
                chatOptionsItem.getLinkedChest()
                        .addMenuItem(InventoryMenuAPI.createItemDynamic()
                        .setName(ChatChannel.getChannel(channel).getName())
                        .setDescription(cp -> { return "This chat is "
                                + (cp.getOptions().isChannelDisabled(channel.toString()) ? (ChatColor.RED + "Disabled") : (ChatColor.GREEN + "Enabled")); })
                        .setDisplayItem(cp -> { return new ItemStack(cp.getOptions().isChannelDisabled(channel.toString()) ? Material.BOOK : Material.WRITABLE_BOOK); })
                        .setAction(cp -> { cp.getOptions().toggleDisabledChannel(channel.toString()); })
                        .setCloseOnAction(false)
                        .setVisibility(cp -> ChatChannel.getChannel(channel).isAvailable(cp)));
            }

            menuItem.getLinkedChest()
                    .addMenuItem(chatOptionsItem);

            /*
            // Post Game Warping
            InventoryMenuItemOption postGameItem = (InventoryMenuItemOption) InventoryMenuAPI
                    .createItemOption(cp -> { return cp.getOptions().getOption(CorePlayerOptions.CPOptions.POST_GAME_WARP); })
                    .setName("Post Game Positioning")
                    .setDisplayItem(Material.ENDER_PEARL)
                    .setAction(cp -> cp.getOptions().nextOption(CorePlayerOptions.CPOptions.POST_GAME_WARP));
            CorePlayerOptions.PlayerOptions pgw = CorePlayerOptions.getOptions(CorePlayerOptions.CPOptions.POST_GAME_WARP.name());
            for (CorePlayerOptions.PlayerOptions.Option o : pgw.getOptions()) {
                postGameItem.addOption(o.displayName, o.displayItem);
            }
            
            menuItem.getLinkedChest()
                    .addMenuItem(postGameItem);
             */
        }
        return menuItem;
    }

}
