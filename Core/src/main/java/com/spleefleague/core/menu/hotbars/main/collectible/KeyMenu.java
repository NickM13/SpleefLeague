/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main.collectible;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayerCollectibles;
import com.spleefleague.core.player.collectible.gear.Gear;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.player.collectible.key.Key;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class KeyMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = CorePlayerCollectibles.createCollectibleContainer(Key.class,
                InventoryMenuAPI.createItemDynamic()
                        .setName("Keys")
                        .setDisplayItem(Material.IRON_HELMET, 1)
                        .setDescription("Keys for opening doors!"));

        InventoryMenuAPI.createItemHotbar(6, "Key")
                .setName(cp -> cp.getCollectibles().getActiveName(Key.class))
                .setDisplayItem(cp -> cp.getCollectibles().getActive(Key.class).getDisplayItem())
                .setDescription(cp -> cp.getCollectibles().getActive(Key.class).getDescription())
                .setAvailability(cp -> !cp.isBattler() && cp.getCollectibles().hasActive(Key.class) && cp.getCollectibles().isEnabled(Key.class))
                .setAction(cp -> cp.getCollectibles().getActive(Key.class).onRightClick(cp));
    }

    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) init();
        return menuItem;
    }

}
