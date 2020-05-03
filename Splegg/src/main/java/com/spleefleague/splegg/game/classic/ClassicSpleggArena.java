/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game.classic;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggArena;
import com.spleefleague.splegg.game.SpleggGun;
import com.spleefleague.splegg.game.SpleggMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class ClassicSpleggArena {
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.GREEN + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "Classic Splegg")
                .setDescription("This is infact a real gamemode")
                .setDisplayItem(Material.EGG)
                .createLinkedContainer("Splegg Menu");
        
        menuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> Splegg.getInstance().queuePlayer(SpleggMode.CLASSIC.getBattleMode(), cp)));
        
        Arenas.getAll(SpleggMode.CLASSIC.getBattleMode()).forEach((String s, Arena arena) -> menuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName(arena.getDisplayName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(cp -> Splegg.getInstance().queuePlayer(SpleggMode.CLASSIC.getBattleMode(), cp, arena))));
        
        menuItem.getLinkedContainer().addStaticItem(SpleggGun.createMenu(), 4, 4);
        
        Splegg.getInstance().getSpleggMenu().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
}
