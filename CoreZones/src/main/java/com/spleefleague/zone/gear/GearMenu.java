package com.spleefleague.zone.gear;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayerCollectibles;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 2/13/2021
 */
public class GearMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = CorePlayerCollectibles.createCollectibleContainer(Gear.class,
                InventoryMenuAPI.createItemDynamic()
                        .setName("Adventure Gear")
                        .setDisplayItem(Material.STRING, 1)
                        .setDescription("Pick some gear to adventure with!")
                        .createLinkedContainer("Adventure Gear"));
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
