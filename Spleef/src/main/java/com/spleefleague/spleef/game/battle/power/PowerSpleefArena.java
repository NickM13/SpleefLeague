/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.leaderboard.LeaderboardCollection;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.LeaderboardMenu;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import com.spleefleague.spleef.game.battle.power.training.PowerTrainingArena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class PowerSpleefArena {
    
    private static final String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;
    
    public static void createMenu(int x, int y) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("&6&lPower Spleef")
                .setDescription(cp -> "A twist on the original 1v1 Spleef Mode. Add unique powers to your Spleefing strategy!" +
                        "\n\n&7&lCurrently Playing: &6" + Spleef.getInstance().getBattleManager(SpleefMode.POWER.getBattleMode()).getPlaying())
                .setDisplayItem(Material.GOLDEN_SHOVEL, 32)
                .createLinkedContainer("Power Spleef Menu");

        menuItem.getLinkedChest()
                .setOpenAction((container, cp2) -> Arenas.fillMenu(Spleef.getInstance(), container, SpleefMode.POWER.getBattleMode()));

        menuItem.getLinkedChest().addStaticItem(PowerTrainingArena.createMenu(), 0, 4);

        menuItem.getLinkedChest().addStaticItem(Abilities.createAbilityMenuItem(Ability.Type.OFFENSIVE), 2, 4);
        menuItem.getLinkedChest().addStaticItem(Abilities.createAbilityMenuItem(Ability.Type.UTILITY), 4, 4);
        menuItem.getLinkedChest().addStaticItem(Abilities.createAbilityMenuItem(Ability.Type.MOBILITY), 6, 4);

        Spleef.getInstance().getSpleefMenu().getLinkedChest().addStaticItem(menuItem, x, y);
    }
    
    public static void initLeaderboard() {
        LeaderboardCollection leaderboard = Core.getInstance().getLeaderboards().get(SpleefMode.POWER.getName());
        InventoryMenuItem menuItem = InventoryMenuAPI.createItemDynamic()
                .setName(mainColor + "Power Spleef")
                .setDescription("View the top players of Power Spleef!")
                .setDisplayItem(Material.GOLDEN_SHOVEL, 32)
                .setLinkedContainer(leaderboard.createMenuContainer());
        
        LeaderboardMenu.getItem()
                .getLinkedChest()
                .addMenuItem(menuItem);
    }
    
}
