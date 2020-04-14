/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.splegg.classic;

import com.spleefleague.core.game.Arena;
import static com.spleefleague.core.game.Arena.getArenas;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.SpleggArena;
import com.spleefleague.spleef.game.SpleggMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class ClassicSpleggArena extends SpleggArena {
    
    public ClassicSpleggArena() {
        this.mode = SpleggMode.CLASSIC.getArenaMode();
    }
    
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
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleggMode.CLASSIC.getArenaMode(), Spleef.getInstance().getPlayers().get(cp))));
        
        getArenas(SpleggMode.CLASSIC.getArenaMode()).forEach((String s, Arena arena) -> menuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName(arena.getDisplayName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleggMode.CLASSIC.getArenaMode(), Spleef.getInstance().getPlayers().get(cp), arena))));
        
        menuItem.getLinkedContainer().addStaticItem(SpleggGun.createMenu(), 4, 4);
        
        Spleef.getInstance().getSpleggMenu().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
}
