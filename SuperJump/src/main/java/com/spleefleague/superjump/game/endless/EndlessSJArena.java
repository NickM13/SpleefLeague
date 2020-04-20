/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.endless;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.Leaderboard;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJArena;
import com.spleefleague.superjump.game.SJMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class EndlessSJArena extends SJArena {
    
    public enum EndlessLeaderboard {
        DAILY("SJ_EL_DAILY"),
        BEST("SJ_EL_ALLTIME");
        
        String name;
        
        EndlessLeaderboard(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    @DBField
    protected Integer jumpcount;
    
    public EndlessSJArena() {
        mode = SJMode.ENDLESS.getArenaMode();
    }
    
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
    
    public static void createMenu(int x, int y) {
        String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(mainColor + "SuperJump: Endless")
                .setDescription("Endless Description.")
                .setDisplayItem(Material.DIAMOND_AXE, 20);
        menuItem.getLinkedContainer()
                .setTitle("SuperJump: Endless");
        
        getArenas(SJMode.ENDLESS.getArenaMode()).forEach((String s, Arena arena) -> menuItem
                .getLinkedContainer()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName(arena.getDisplayName())
                        .setDescription(cp -> arena.getDescription())
                        .setDisplayItem(cp -> { return new ItemStack(Material.ENDER_EYE); })
                        .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.ENDLESS.getArenaMode(), cp, arena))));
        
        SuperJump.getInstance().getSJMenuItem().getLinkedContainer().addMenuItem(menuItem, x, y);
    }
    
    public int getJumpCount() {
        return jumpcount;
    }
    
}
