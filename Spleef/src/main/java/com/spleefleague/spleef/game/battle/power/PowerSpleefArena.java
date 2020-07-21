/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power;

import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.leaderboard.LeaderboardCollection;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.LeaderboardMenu;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class PowerSpleefArena {
    
    private static final String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
    
    public static void createMenu(int x, int y) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("&6&lPower Spleef")
                .setDescription("A twist on the original 1v1 Spleef Mode. Add unique powers to your Spleefing strategy!" +
                        "\n\n&7&lCurrently Playing: &6" + Spleef.getInstance().getBattleManager(SpleefMode.POWER.getBattleMode()).getCurrentlyPlaying())
                .setDisplayItem(Material.GOLDEN_SHOVEL, 32)
                .createLinkedContainer("Power Spleef Menu");

        menuItem.getLinkedChest()
                .setPageBoundaries(1, 3, 1, 7)
                .setOpenAction((container, cp2) -> {
                    container.clearUnsorted();
                    container.addMenuItem(InventoryMenuAPI.createItem()
                            .setName("&a&lRandom Arena")
                            .setDisplayItem(new ItemStack(Material.EMERALD))
                            .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.POWER.getBattleMode(), cp)));

                    Arenas.getAll(SpleefMode.POWER.getBattleMode()).values().forEach(arena -> {
                        menuItem.getLinkedChest().addMenuItem(arena.createMenu((cp -> Spleef.getInstance().queuePlayer(SpleefMode.POWER.getBattleMode(), cp, arena))));
                    });
                });

        menuItem.getLinkedChest().addStaticItem(AbilityOffensive.createMenu(), 1, 4);
        menuItem.getLinkedChest().addStaticItem(AbilityUtility.createMenu(), 4, 4);
        menuItem.getLinkedChest().addStaticItem(AbilityMobility.createMenu(), 7, 4);
        Spleef.getInstance().getSpleefMenu().getLinkedChest().addMenuItem(menuItem, x, y);
    }
    
    public static void initLeaderboard(int x, int y) {
        LeaderboardCollection leaderboard = Leaderboards.get(SpleefMode.POWER.getName());
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "Power Spleef")
                .setDescription("View the top players of Power Spleef!")
                .setDisplayItem(Material.GOLDEN_SHOVEL, 32)
                .setLinkedContainer(leaderboard.createMenuContainer());
        
        LeaderboardMenu.getItem()
                .getLinkedChest()
                .addMenuItem(menuItem, x, y);
    }
    
}
