/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.practice;

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
public class PracticeSJArena extends SJArena {
    
    public PracticeSJArena() {
        mode = SJMode.PRACTICE.getArenaMode();
    }
    
    public static InventoryMenuItem createMenu() {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenuItem spleefMenuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "SuperJump: Practice")
                .setDescription("Practice Description.")
                .setDisplayItem(Material.DIAMOND_AXE, 20)
                .createLinkedContainer("Practice SuperJump Menu");
        
        InventoryMenuItem mapMenuItem = InventoryMenuAPI.createItem()
                .setName("Map Select: Practice Spleef")
                .setDisplayItem(new ItemStack(Material.FILLED_MAP))
                .createLinkedContainer("Map Select: Practice Spleef");
        
        mapMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.PRACTICE.getArenaMode(), SuperJump.getInstance().getPlayers().get(cp))));
        
        getArenas(SJMode.PRACTICE.getArenaMode()).forEach((String s, Arena arena) -> mapMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName(arena.getDisplayName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.PRACTICE.getArenaMode(), SuperJump.getInstance().getPlayers().get(cp), arena))));
        
        spleefMenuItem.getLinkedContainer().addMenuItem(mapMenuItem, 0);
        
        return spleefMenuItem;
    }

}
