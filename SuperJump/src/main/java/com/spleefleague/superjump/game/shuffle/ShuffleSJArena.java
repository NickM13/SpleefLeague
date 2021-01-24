/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.shuffle;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class ShuffleSJArena {
    
     public enum ShuffleDifficulty {
         EASY(0),
         MEDIUM(1),
         HARD(2),
         INSANE(3);
         
         int difficulty;
         
         ShuffleDifficulty(int dif) {
             difficulty = dif;
         }
     }
    
    @DBField
    protected Integer jumpcount;
    @DBField
    protected ShuffleDifficulty difficulty;
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItemDynamic()
                .setName(mainColor + "SuperJump: Shuffle")
                .setDescription("Shuffle Description")
                .setDisplayItem(Material.DIAMOND_AXE, 24)
                .createLinkedContainer("Shuffle SuperJump Menu");
        
        Arenas.getAll(SJMode.SHUFFLE.getBattleMode()).forEach((String s, Arena arena) -> menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(arena.getName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(Material.DIAMOND_AXE, 16)
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.SHUFFLE.getBattleMode(), cp, arena))));
        
        SuperJump.getInstance().getSJMenuItem().getLinkedChest().addStaticItem(menuItem, x, y);
    }
    
}
