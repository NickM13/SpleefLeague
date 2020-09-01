package com.spleefleague.core.menu.hotbars;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;

/**
 * @author NickM13
 * @since 4/18/2020
 */
public class HeldItemHotbar {
    
    private static InventoryMenuItemHotbar menuItem = null;
    
    public static void init() {
        menuItem = (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(8, "helditem")
                .setName(cp -> cp.getCollectibles().getHeldItem().getName())
                .setDescription(cp -> cp.getCollectibles().getHeldItem().getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getHeldItem().getDisplayItem())
                .setAction(cp -> { cp.getCollectibles().getHeldItem().onRightClick(cp); })
                .setAvailability(cp -> cp.isInGlobal() && cp.getCollectibles().hasHeldItem());
    }
    
    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItemHotbar getItemHotbar() {
        if (menuItem == null) init();
        return menuItem;
    }
    
}
