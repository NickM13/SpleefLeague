/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.party;

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
public class PartySJArena {
    
    public static InventoryMenuItem createMenu() {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenuItem spleefMenu = InventoryMenuAPI.createItemDynamic()
                .setName(mainColor + "SuperJump: Party")
                .setDescription("Party Description.")
                .setDisplayItem(Material.DIAMOND_AXE, 20)
                .createLinkedContainer("Party SuperJump Menu");
        
        InventoryMenuItem mapMenuItem = InventoryMenuAPI.createItemDynamic()
                .setName("Map Select: Party Spleef")
                .setDisplayItem(new ItemStack(Material.FILLED_MAP))
                .createLinkedContainer("Map Select: Party Spleef");
        
        mapMenuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.PARTY.getBattleMode(), cp)));
        
        Arenas.getAll(SJMode.PARTY.getBattleMode()).forEach((String s, Arena arena) -> mapMenuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(arena.getName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.PARTY.getBattleMode(), cp, arena))));
        
        spleefMenu.getLinkedChest().addStaticItem(mapMenuItem, 0);
        
        return spleefMenu;
    }

}
