/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game.classic;

import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.leaderboard.LeaderboardCollection;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.LeaderboardMenu;
import com.spleefleague.splegg.Splegg;
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
        String mainColor = ChatColor.GOLD + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "Classic Splegg")
                .setDescription("Test your might against another player in this 1v1 competition of precision and movement.")
                .setDisplayItem(Material.EGG)
                .createLinkedContainer("Classic Splegg");

        menuItem.getLinkedChest()
                .setPageBoundaries(1, 3, 1, 7)
                .setOpenAction((container, cp2) -> {
                    container.clearUnsorted();
                    container.addMenuItem(InventoryMenuAPI.createItem()
                            .setName("&a&lRandom Arena")
                            .setDescription("Join the queue for a random " + mainColor + "Classic Splegg &7map!")
                            .setDisplayItem(new ItemStack(Material.EMERALD))
                            .setAction(cp -> Splegg.getInstance().queuePlayer(SpleggMode.CLASSIC.getBattleMode(), cp)));

                    Arenas.getAll(SpleggMode.CLASSIC.getBattleMode()).values().forEach(arena -> {
                        menuItem.getLinkedChest().addMenuItem(arena.createMenu((cp -> Splegg.getInstance().queuePlayer(SpleggMode.CLASSIC.getBattleMode(), cp, arena))));
                    });
                });

        menuItem.getLinkedChest().addStaticItem(SpleggGun.createMenu("s1", "s2"), 3, 4);
        menuItem.getLinkedChest().addStaticItem(SpleggGun.createMenu("s2", "s1"), 5, 4);

        Splegg.getInstance().getSpleggMenu().getLinkedChest().addMenuItem(menuItem, x, y);
    }

    public static void initLeaderboard(int x, int y) {
        LeaderboardCollection leaderboard = Leaderboards.get(SpleggMode.CLASSIC.getName());
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("&6&lClassic Splegg")
                .setDescription("View the top players of Classic Splegg!")
                .setDisplayItem(Material.EGG)
                .setLinkedContainer(leaderboard.createMenuContainer());

        LeaderboardMenu.getItem()
                .getLinkedChest()
                .addMenuItem(menuItem, x, y);
    }
    
}
