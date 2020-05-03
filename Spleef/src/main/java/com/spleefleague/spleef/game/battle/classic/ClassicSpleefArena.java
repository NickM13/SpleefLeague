/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.classic;

import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.leaderboard.LeaderboardCollection;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.LeaderboardMenu;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class ClassicSpleefArena {
    
    private static final String mainColor = ChatColor.GREEN + "" + ChatColor.BOLD;
    
    public static void createMenu(int x, int y) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "Classic Spleef")
                .setDescription(cp -> "The classic version in which you duel a single opponent with a basic diamond shovel."
                        + "\n\nOngoing Battles: " + Spleef.getInstance().getBattleManager(SpleefMode.CLASSIC.getBattleMode()).getOngoingBattles()
                        + "\n\nIngame Players: " + Spleef.getInstance().getBattleManager(SpleefMode.CLASSIC.getBattleMode()).getIngamePlayers())
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561)
                .createLinkedContainer("Classic Spleef Menu");
        
        menuItem.getLinkedContainer()
                .setOpenAction((container, cp2) -> {
                    container.clearUnsorted();
                    container.addMenuItem(InventoryMenuAPI.createItem()
                            .setName("Random Arena")
                            .setDisplayItem(new ItemStack(Material.EMERALD))
                            .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.CLASSIC.getBattleMode(), cp)));
    
                    Arenas.getAll(SpleefMode.CLASSIC.getBattleMode()).values().forEach(arena ->
                            container.addMenuItem(InventoryMenuAPI.createItem()
                            .setName(arena.getDisplayName())
                            .setDescription(cp -> arena.getDescription())
                            .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                            .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.CLASSIC.getBattleMode(), cp, arena))));
                });
        
        Spleef.getInstance().getSpleefMenu().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
    public static void initLeaderboard(int x, int y) {
        LeaderboardCollection leaderboard = Leaderboards.get(SpleefMode.CLASSIC.getName());
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("Classic Spleef Leaderboard!!")
                .setDescription("Description")
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561)
                .setLinkedContainer(leaderboard.createMenuContainer());
        
        LeaderboardMenu.getItem()
                .getLinkedContainer()
                .addMenuItem(menuItem, x, y);
    }
    
}
