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
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class ClassicSpleefArena {

    public static void createMenu(int x, int y) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("&6&lClassic Spleef")
                .setDescription(cp -> "Take to the field in SpleefLeague's premier gamemode - Classic Spleef!" +
                        " Dig out blocks from under your opponent before they are able to do the same to you!" +
                        "\n\nAffixes: " + ClassicSpleefAffixes.getActiveDisplayNames() +
                        "\n\n&7&lCurrently Playing: &6" + Spleef.getInstance().getBattleManager(SpleefMode.CLASSIC.getBattleMode()).getPlaying())
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1)
                .createLinkedContainer("Classic Spleef Menu");

        menuItem.getLinkedChest().setOpenAction((container, cp2) -> Arenas.fillMenu(Spleef.getInstance(), container, SpleefMode.CLASSIC.getBattleMode()));
        
        Spleef.getInstance().getSpleefMenu().getLinkedChest().addStaticItem(menuItem, x, y);
    }
    
    public static void initLeaderboard() {
        LeaderboardMenu.addLeaderboardMenu(SpleefMode.CLASSIC.getBattleMode());
    }
    
}
