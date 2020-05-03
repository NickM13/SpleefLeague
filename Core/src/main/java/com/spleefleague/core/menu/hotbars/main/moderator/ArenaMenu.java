package com.spleefleague.core.menu.hotbars.main.moderator;

import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 5/1/2020
 */
public class ArenaMenu {
    
    private static InventoryMenuItem menuItem = null;
    
    public static void init() {
        menuItem = InventoryMenuAPI.createItem()
                .setName("Arenas")
                .setDisplayItem(Material.GRASS_BLOCK)
                .setDescription("For editing arena values of all gamemodes")
                .createLinkedContainer("Arenas");
        
        menuItem.getLinkedContainer()
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    for (BattleMode battleMode : BattleMode.getAllModes()) {
                        container.addMenuItem(InventoryMenuAPI.createItem()
                                .setName(battleMode.getDisplayName())
                                .setDescription("")
                                .setDisplayItem(Material.SAND)
                                .setLinkedContainer(battleMode.createEditMenu()));
                    }
                });
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
