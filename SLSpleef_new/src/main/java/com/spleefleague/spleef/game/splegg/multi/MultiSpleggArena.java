/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.splegg.multi;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleggArena;
import com.spleefleague.spleef.game.SpleggMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class MultiSpleggArena extends SpleggArena {

    public MultiSpleggArena() {
        this.mode = SpleggMode.MULTI.getArenaMode();
    }
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.GREEN + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "MultiSplegg")
                .setDescription("This is infact a real gamemode, with lots of people!")
                .setDisplayItem(Material.CHICKEN_SPAWN_EGG)
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleggMode.MULTI.getArenaMode(), Spleef.getInstance().getPlayers().get(cp)));
        
        Spleef.getInstance().getSpleggMenu().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
}
