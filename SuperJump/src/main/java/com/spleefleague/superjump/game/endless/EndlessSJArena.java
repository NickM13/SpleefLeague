/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.endless;

import com.spleefleague.core.game.leaderboard.LeaderboardCollection;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.LeaderboardMenu;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class EndlessSJArena {
    
    /*
    public static void initLeaderboard() {
        Leaderboard.init(EndlessSJArena.EndlessLeaderboard.DAILY.getName(),
                Leaderboard.LeaderboardStyle.DAILY,
                "SJ Endless Daily",
                InventoryMenuUtils.createCustomItem(Material.DARK_OAK_SIGN),
                "Endless Daily Description");
        Leaderboard.init(EndlessSJArena.EndlessLeaderboard.BEST.getName(),
                Leaderboard.LeaderboardStyle.ALLTIME,
                "SJ Endless Alltime",
                InventoryMenuUtils.createCustomItem(Material.DARK_OAK_SIGN),
                "Endless Alltime Description");
    }
    */
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "SuperJump: Endless")
                .setDescription("Endless Description.")
                .setDisplayItem(Material.DIAMOND_AXE, 20)
                .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.ENDLESS.getBattleMode(), cp));
        
        SuperJump.getInstance().getSJMenuItem().getLinkedChest().addMenuItem(menuItem, x, y);
    }
    
    public static void initLeaderboard(int x, int y) {
        LeaderboardCollection bestLevelLeaderboard = Leaderboards.get(SJMode.ENDLESS.getName());
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("SuperJump Endless")
                .setDescription("View the top players of SuperJump Endless!")
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561)
                .setLinkedContainer(bestLevelLeaderboard.createMenuContainer());
        
        LeaderboardMenu.getItem()
                .getLinkedChest()
                .addMenuItem(menuItem, x, y);
    }
    
}
