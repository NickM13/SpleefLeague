/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefArena;
import com.spleefleague.spleef.game.SpleefMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class PowerSpleefArena extends SpleefArena {
    
    public PowerSpleefArena() {
        mode = SpleefMode.POWER.getArenaMode();
    }
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "Power Spleef")
                .setDescription("A twist on the original 1v1 Spleef Mode.  Add unique powers to your Spleefing strategy!")
                .setDisplayItem(Material.GOLDEN_SHOVEL, 32)
                .createLinkedContainer("Power Spleef Menu");
        
        menuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.POWER.getArenaMode(), cp)));
        
        getArenas(SpleefMode.POWER.getArenaMode()).forEach((String s, Arena arena) -> {
            menuItem.getLinkedContainer().addMenuItem(arena.createMenu((cp -> {
                Spleef.getInstance().queuePlayer(SpleefMode.POWER.getArenaMode(), cp, arena);
            })));
        });
        
        menuItem.getLinkedContainer().addStaticItem(Power.createMenu(0), 1, 4);
        menuItem.getLinkedContainer().addStaticItem(Power.createMenu(1), 3, 4);
        menuItem.getLinkedContainer().addStaticItem(Power.createMenu(2), 5, 4);
        menuItem.getLinkedContainer().addStaticItem(Power.createMenu(3), 7, 4);
        
        menuItem.getLinkedContainer().addMenuItem(menuItem, 0);
        
        Spleef.getInstance().getSpleefMenu().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
}
