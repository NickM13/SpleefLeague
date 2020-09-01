/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.conquest;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.superjump.SuperJump;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class ConquestSJArena {
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "SuperJump: Conquest")
                .setDescription("Conquest Description.")
                .setDisplayItem(Material.DIAMOND_AXE, 21)
                .createLinkedContainer("SuperJump: Conquest");
        
        ConquestPack.init();
        for (ConquestPack pack : ConquestPack.getAllPacks()) {
            menuItem.getLinkedChest()
                    .addMenuItem(pack.createMenu());
        }
        
        SuperJump.getInstance().getSJMenuItem().getLinkedChest().addMenuItem(menuItem, x, y);
    }
}
