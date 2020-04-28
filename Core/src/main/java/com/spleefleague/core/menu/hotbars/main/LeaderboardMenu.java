/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.Leaderboard;
import com.spleefleague.core.game.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;

import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class LeaderboardMenu {
    
    protected static InventoryMenuItem menuItem = null;
    
    /**
     * Gets the menu item for this menu, if it doesn't exist already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) {
            // Options Menus
            menuItem = InventoryMenuAPI.createItem()
                    .setName("Leaderboards")
                    .setDisplayItem(new ItemStack(Material.OAK_SIGN))
                    .setDescription("View the Top Players of SpleefLeague!")
                    .createLinkedContainer("Leaderboards");
            menuItem.getLinkedContainer()
                    .setOpenAction((container, cp) -> {
                        container.clearUnsorted();
                        int i = 0;
                        for (Map.Entry<String, Leaderboard> lb : Leaderboards.getLeaderboards().entrySet()) {
                            InventoryMenuItem leaderboardItem = InventoryMenuAPI.createItem()
                                    .setName(lb.getValue().getDisplayName())
                                    .setDisplayItem(lb.getValue().getDisplayItem())
                                    .setDescription(lb.getValue().getDescription())
                                    .createLinkedContainer(lb.getValue().getDisplayName());
                            leaderboardItem.getLinkedContainer()
                                    .setOpenAction((container2, cp2) -> {
                                            container2.clearUnsorted();
                                            int j = 0;
                                            for (UUID uuid : lb.getValue().getPlayers()) {
                                                CorePlayer cp3 = Core.getInstance().getPlayers().getOffline(uuid);
                                                container2.addMenuItem(InventoryMenuAPI.createItem()
                                                        .setName(p -> cp3.getDisplayName() + " #" + lb.getValue().getPlaceOf(uuid))
                                                        .setDisplayItem(p -> InventoryMenuUtils.createCustomSkull(cp3.getUniqueId()))
                                                        .setCloseOnAction(false));
                                                j++;
                                            }
                                    });
                            container.addMenuItem(leaderboardItem);
                            i++;
                        }
                    });
        }
        return menuItem;
    }

}
