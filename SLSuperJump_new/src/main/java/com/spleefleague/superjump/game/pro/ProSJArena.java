/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.pro;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJArena;
import com.spleefleague.superjump.game.SJMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class ProSJArena extends SJArena {
    
    public ProSJArena() {
        mode = SJMode.PRO.getArenaMode();
    }
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "SuperJump: Pro")
                .setDescription("Pro Description.")
                .setDisplayItem(Material.DIAMOND_AXE, 23)
                .createLinkedContainer("Pro SuperJump Menu");
        
        menuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.PRO.getArenaMode(), SuperJump.getInstance().getPlayers().get(cp))));
        
        getArenas(SJMode.PRO.getArenaMode()).forEach((String s, Arena arena) -> menuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName(arena.getDisplayName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.PRO.getArenaMode(), SuperJump.getInstance().getPlayers().get(cp), arena))));
        
        menuItem.getLinkedContainer().addMenuItem(menuItem, 0);
        
        SuperJump.getInstance().getSJMenuItem().getLinkedContainer().addMenuItem(menuItem, x, y);
    }

}
