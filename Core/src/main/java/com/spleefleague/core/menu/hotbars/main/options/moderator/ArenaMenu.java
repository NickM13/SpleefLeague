package com.spleefleague.core.menu.hotbars.main.options.moderator;

import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.rank.CoreRank;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 5/1/2020
 */
public class ArenaMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("Arena Setup")
                .setDisplayItem(Material.GRASS_BLOCK)
                .setDescription("For editing arena values of all gamemodes")
                .setAvailability(cp -> cp.getRank().hasPermission(CoreRank.DEVELOPER))
                .createLinkedContainer("Arenas");

        menuItem.getLinkedChest()
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    for (BattleMode battleMode : BattleMode.getAllModes()) {
                        container.addMenuItem(InventoryMenuAPI.createItemDynamic()
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
