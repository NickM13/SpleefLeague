/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menus;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.util.Warp;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class DonorMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            menuItem = InventoryMenuAPI.createItem()
                    .setName("Donor Warps")
                    .setDisplayItem(Material.EMERALD)
                    .setDescription("Warps to the donations room")
                    .setAvailability(cp -> cp.getRank().hasPermission(Rank.DONOR_1))
                    .setAction(cp -> { cp.warp(Warp.getWarp("Donator")); });
        }
        return menuItem;
    }
    
}
