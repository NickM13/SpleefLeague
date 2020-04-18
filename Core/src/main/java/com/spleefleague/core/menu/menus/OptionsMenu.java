/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.menus;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuItemOption;
import com.spleefleague.core.player.CorePlayerOptions;
import com.spleefleague.core.player.PlayerOptions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class OptionsMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            // Options Menus
            menuItem = InventoryMenuAPI.createItem()
                    .setName("Options")
                    .setDisplayItem(new ItemStack(Material.WRITABLE_BOOK))
                    .setDescription("Customize your SpleefLeague experience")
                    .createLinkedContainer("Options Menu");

            // Chat Options Menus
            InventoryMenuItem chatOptionsItem = InventoryMenuAPI.createItem()
                    .setName("Chat Channels")
                    .setDisplayItem(new ItemStack(Material.WRITABLE_BOOK))
                    .setDescription("Toggle Chat Channels");

            for (ChatChannel.Channel channel : ChatChannel.Channel.values()) {
                chatOptionsItem.getLinkedContainer()
                        .addMenuItem(InventoryMenuAPI.createItem()
                        .setName(ChatChannel.getChannel(channel).getName())
                        .setDescription(cp -> { return "This chat is "
                                + (cp.getOptions().isChannelDisabled(channel.toString()) ? (ChatColor.RED + "Disabled") : (ChatColor.GREEN + "Enabled")); })
                        .setDisplayItem(cp -> { return new ItemStack(cp.getOptions().isChannelDisabled(channel.toString()) ? Material.BOOK : Material.WRITABLE_BOOK); })
                        .setAction(cp -> { cp.getOptions().toggleDisabledChannel(channel.toString()); })
                        .setCloseOnAction(false)
                        .setVisibility(cp -> ChatChannel.getChannel(channel).isAvailable(cp)));
            }

            menuItem.getLinkedContainer()
                    .addMenuItem(chatOptionsItem);
            
            // Post Game Warping
            InventoryMenuItemOption postGameItem = (InventoryMenuItemOption) InventoryMenuAPI
                    .createItemOption(cp -> { return cp.getOptions().getOption(CorePlayerOptions.CPOptions.POST_GAME_WARP); })
                    .setName("Post Game Positioning")
                    .setDisplayItem(Material.ENDER_PEARL)
                    .setAction(cp -> cp.getOptions().nextOption(CorePlayerOptions.CPOptions.POST_GAME_WARP));
            PlayerOptions pgw = CorePlayerOptions.getOptions(CorePlayerOptions.CPOptions.POST_GAME_WARP.name());
            for (PlayerOptions.Option o : pgw.getOptions()) {
                postGameItem.addOption(o.displayName, o.displayItem);
            }
            
            menuItem.getLinkedContainer()
                    .addMenuItem(postGameItem);
        }
        return menuItem;
    }

}
