/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.classic;

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
public class ClassicSpleefArena extends SpleefArena {
    
    public ClassicSpleefArena() {
        mode = SpleefMode.CLASSIC.getArenaMode();
    }
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.GREEN + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "Classic Spleef")
                .setDescription(cp -> "The classic version in which you duel a single opponent with a basic diamond shovel."
                        + "\n\nOngoing Battles: " + Spleef.getInstance().getBattleManager(SpleefMode.CLASSIC.getArenaMode()).getOngoingBattles()
                        + "\n\nIngame Players: " + Spleef.getInstance().getBattleManager(SpleefMode.CLASSIC.getArenaMode()).getIngamePlayers())
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561)
                .createLinkedContainer("Classic Spleef Menu");
        
        menuItem.getLinkedContainer()
                .setOpenAction((container, cp2) -> {
                    container.clearUnsorted();
                    container.addMenuItem(InventoryMenuAPI.createItem()
                            .setName("Random Arena")
                            .setDisplayItem(new ItemStack(Material.EMERALD))
                            .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.CLASSIC.getArenaMode(), cp)));

                    Arena.getArenas(SpleefMode.CLASSIC.getArenaMode()).forEach((String s, Arena arena) -> container.addMenuItem(InventoryMenuAPI.createItem()
                            .setName(arena.getDisplayName())
                            .setDescription(cp -> arena.getDescription())
                            .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                            .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.CLASSIC.getArenaMode(), cp, arena))));
                });
        
        Spleef.getInstance().getSpleefMenu().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
}
