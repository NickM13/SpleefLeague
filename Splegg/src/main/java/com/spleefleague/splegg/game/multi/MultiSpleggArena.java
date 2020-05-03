/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game.multi;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggArena;
import com.spleefleague.splegg.game.SpleggMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class MultiSpleggArena extends SpleggArena {
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.GREEN + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "MultiSplegg")
                .setDescription("This is infact a real gamemode, with lots of people!")
                .setDisplayItem(Material.CHICKEN_SPAWN_EGG)
                .setAction(cp -> Splegg.getInstance().queuePlayer(SpleggMode.MULTI.getBattleMode(), cp));

        Splegg.getInstance().getSpleggMenu().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
}
