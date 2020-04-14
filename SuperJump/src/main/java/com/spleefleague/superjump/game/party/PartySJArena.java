/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.party;

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
public class PartySJArena extends SJArena {
    
    public PartySJArena() {
        mode = SJMode.PARTY.getArenaMode();
    }
    
    public static InventoryMenuItem createMenu() {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenuItem spleefMenu = InventoryMenuAPI.createItem()
                .setName(mainColor + "SuperJump: Party")
                .setDescription("Party Description.")
                .setDisplayItem(Material.DIAMOND_AXE, 20)
                .createLinkedContainer("Party SuperJump Menu");
        
        InventoryMenuItem mapMenuItem = InventoryMenuAPI.createItem()
                .setName("Map Select: Party Spleef")
                .setDisplayItem(new ItemStack(Material.FILLED_MAP))
                .createLinkedContainer("Map Select: Party Spleef");
        
        mapMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Random Arena")
                .setDisplayItem(new ItemStack(Material.EMERALD))
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.PARTY.getArenaMode(), SuperJump.getInstance().getPlayers().get(cp))));
        
        getArenas(SJMode.PARTY.getArenaMode()).forEach((String s, Arena arena) -> mapMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName(arena.getDisplayName())
                .setDescription(cp -> arena.getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.PARTY.getArenaMode(), SuperJump.getInstance().getPlayers().get(cp), arena))));
        
        spleefMenu.getLinkedContainer().addMenuItem(mapMenuItem, 0);
        
        return spleefMenu;
    }

}
