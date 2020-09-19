/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.bonanza;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class BonanzaSpleefArena {
    
    public static void createMenu(int x, int y) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("&6&lBonanzaSpleef")
                .setDescription("Drop in anytime to this fast paced, constant battleground of Spleef glory!" +
                        "  Compete for kill streaks against other players and become a champion." +
                        "\n\nYou may queue for other games while playing Bonanza Spleef." +
                        "\n\n&7&lCurrently Playing: &6" + Spleef.getInstance().getBattleManager(SpleefMode.BONANZA.getBattleMode()).getPlaying())
                .setDisplayItem(Material.DIAMOND_SHOVEL, 1561)
                .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.BONANZA.getBattleMode(), cp));
        
        Spleef.getInstance().getSpleefMenu().getLinkedChest().addMenuItem(menuItem, x, y);
    }
    
}
