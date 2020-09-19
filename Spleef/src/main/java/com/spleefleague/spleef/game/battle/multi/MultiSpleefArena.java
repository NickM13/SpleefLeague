/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.multi;

import com.spleefleague.core.game.leaderboard.LeaderboardCollection;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.LeaderboardMenu;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class MultiSpleefArena {
    
    public static void createMenu(int x, int y) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("&6&lMultispleef")
                .setDescription("Fight your dominance in this free-for-all edition of Spleef." +
                        "\n\n&7&lCurrently Playing: &6" + Spleef.getInstance().getBattleManager(SpleefMode.MULTI.getBattleMode()).getPlaying())
                .setDisplayItem(Material.SHEARS, 238)
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.MULTI.getBattleMode(), cp));
        
        Spleef.getInstance().getSpleefMenu().getLinkedChest().addMenuItem(menuItem, x, y);
    }
    
    public static void initLeaderboard(int x, int y) {
        LeaderboardCollection leaderboard = Leaderboards.get(SpleefMode.MULTI.getName());
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("&6&lMultispleef")
                .setDescription("View the top players of Multispleef!")
                .setDisplayItem(Material.SHEARS, 238)
                .setLinkedContainer(leaderboard.createMenuContainer());
        
        LeaderboardMenu.getItem()
                .getLinkedChest()
                .addMenuItem(menuItem, x, y);
    }
    
}
