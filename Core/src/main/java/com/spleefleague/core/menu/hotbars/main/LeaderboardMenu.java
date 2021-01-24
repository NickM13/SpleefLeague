/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.leaderboard.LeaderboardCollection;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class LeaderboardMenu {
    
    protected static InventoryMenuItem menuItem = null;
    
    /**
     * Gets the menu item for this menu, if it doesn't exist already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            // Options Menus
            menuItem = InventoryMenuAPI.createItemDynamic()
                    .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Leaderboards")
                    .setDisplayItem(Material.OAK_SIGN, 1)
                    .setSelectedItem(Material.OAK_SIGN, 2)
                    .setDescription("View the Top Players of SpleefLeague!")
                    .createLinkedContainer("Leaderboards");
        }
        return menuItem;
    }

    public static void addLeaderboardMenu(BattleMode mode) {
        LeaderboardCollection leaderboard = Core.getInstance().getLeaderboards().get(mode.getName());

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(mode.getDisplayName())
                .setDescription("View the top players of " + mode.getDisplayName())
                .setDisplayItem(mode.getDisplayItem())
                .setLinkedContainer(leaderboard.createMenuContainer()));
    }

}
