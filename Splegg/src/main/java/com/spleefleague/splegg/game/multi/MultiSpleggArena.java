/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game.multi;

import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.leaderboard.LeaderboardCollection;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.LeaderboardMenu;
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
public class MultiSpleggArena extends SpleggArena {
    
    public static void createMenu(int x, int y) {
        /*
        String mainColor = ChatColor.GREEN + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "MultiSplegg")
                .setDescription("This is infact a real gamemode, with lots of people!")
                .setDisplayItem(Material.CHICKEN_SPAWN_EGG)
                .setAction(cp -> Splegg.getInstance().queuePlayer(SpleggMode.MULTI.getBattleMode(), cp));

        Splegg.getInstance().getSpleggMenu().getLinkedChest().addMenuItem(menuItem, x, y);
        */
        String mainColor = ChatColor.GOLD + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "Multisplegg")
                .setDescription("Take your skills to the next level in this free-for-all multiplayer edition of Splegg!")
                .setDisplayItem(Material.CHICKEN_SPAWN_EGG)
                .createLinkedContainer("Multisplegg");

        menuItem.getLinkedChest()
                .setPageBoundaries(1, 3, 1, 7)
                .setOpenAction((container, cp2) -> {
                    container.clearUnsorted();
                    container.addMenuItem(InventoryMenuAPI.createItem()
                            .setName("&a&lRandom Arena")
                            .setDisplayItem(new ItemStack(Material.EMERALD))
                            .setAction(cp -> Splegg.getInstance().queuePlayer(SpleggMode.MULTI.getBattleMode(), cp)));

                    Arenas.getAll(SpleggMode.MULTI.getBattleMode()).values().forEach(arena -> {
                        menuItem.getLinkedChest().addMenuItem(arena.createMenu((cp -> Splegg.getInstance().queuePlayer(SpleggMode.MULTI.getBattleMode(), cp, arena))));
                    });
                });

        menuItem.getLinkedChest().addStaticItem(SpleggGun.createMenu("m1", "m2"), 3, 4);
        menuItem.getLinkedChest().addStaticItem(SpleggGun.createMenu("m2", "m1"), 5, 4);

        Splegg.getInstance().getSpleggMenu().getLinkedChest().addMenuItem(menuItem, x, y);
    }

    public static void initLeaderboard(int x, int y) {
        LeaderboardCollection leaderboard = Leaderboards.get(SpleggMode.MULTI.getName());
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("&6&lMultisplegg")
                .setDescription("View the top players of Multisplegg!")
                .setDisplayItem(Material.CHICKEN_SPAWN_EGG)
                .setLinkedContainer(leaderboard.createMenuContainer());

        LeaderboardMenu.getItem()
                .getLinkedChest()
                .addMenuItem(menuItem, x, y);
    }
    
}
