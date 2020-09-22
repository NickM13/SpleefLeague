/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.classic;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.leaderboard.LeaderboardCollection;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.LeaderboardMenu;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class ClassicSpleefArena {

    public static void createMenu(int x, int y) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("&6&lClassic Spleef")
                .setDescription(cp -> "The classic version in which you duel a single opponent with a basic diamond shovel." +
                        "\n\n&7&lCurrently Playing: &6" + Spleef.getInstance().getBattleManager(SpleefMode.CLASSIC.getBattleMode()).getPlaying())
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1)
                .createLinkedContainer("Classic Spleef Menu");
        
        menuItem.getLinkedChest()
                .setPageBoundaries(1, 3, 1, 7)
                .setOpenAction((container, cp2) -> {
                    container.clearUnsorted();
                    container.addMenuItem(InventoryMenuAPI.createItem()
                            .setName("&a&lRandom Arena")
                            .setDisplayItem(new ItemStack(Material.EMERALD))
                            .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.CLASSIC.getBattleMode(), cp)));

                    Arenas.getAll(SpleefMode.CLASSIC.getBattleMode()).values().forEach(arena -> {
                        menuItem.getLinkedChest().addMenuItem(arena.createMenu((cp -> Spleef.getInstance().queuePlayer(SpleefMode.CLASSIC.getBattleMode(), cp, arena))));
                    });
                });
        
        Spleef.getInstance().getSpleefMenu().getLinkedChest().addMenuItem(menuItem, x, y);
    }
    
    public static void initLeaderboard(int x, int y) {
        LeaderboardCollection leaderboard = Core.getInstance().getLeaderboards().get(SpleefMode.CLASSIC.getName());

        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("&6&lClassic Spleef")
                .setDescription("View the top players of Classic Spleef!")
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1)
                .setLinkedContainer(leaderboard.createMenuContainer());
        
        LeaderboardMenu.getItem()
                .getLinkedChest()
                .addMenuItem(menuItem, x, y);
    }
    
}
