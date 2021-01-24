/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.classic;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class ClassicSJArena {
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItemDynamic()
                .setName(mainColor + "SuperJump: Classic")
                .setDescription("A Very Classy GameMode.")
                .setDisplayItem(Material.DIAMOND_AXE, 22)
                .createLinkedContainer("SuperJump: Classic");
        
        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemDynamic()
                        .setName("Random Arena")
                        .setDisplayItem(Material.EMERALD));
    
        Arenas.getAll(SJMode.CLASSIC.getBattleMode()).forEach((String s, Arena arena) -> menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(arena.getName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> new ItemStack(Material.FILLED_MAP))
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.CLASSIC.getBattleMode(), cp, arena))));
        
        SuperJump.getInstance().getSJMenuItem().getLinkedChest().addStaticItem(menuItem, x, y);
    }
    
}
