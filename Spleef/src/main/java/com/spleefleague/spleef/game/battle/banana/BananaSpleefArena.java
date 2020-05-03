/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.banana;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class BananaSpleefArena {
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.GREEN + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "BananaSpleef")
                .setDescription("Multispleef but Banana Mode!!!1!111!!!")
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561)
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.BONANZA.getBattleMode(), cp));
        
        Spleef.getInstance().getSpleefMenu().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
}
