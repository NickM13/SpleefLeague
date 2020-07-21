package com.spleefleague.core.world.build.tool;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.core.world.build.BuildWorldPlayer;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class SelectTool {
    
    public static void init() {
        InventoryMenuItemHotbar hotbarItem = (InventoryMenuItemHotbar) InventoryMenuAPI
                .createItemHotbar(8, "BUILD_TOOL_PLACEABLES")
                .setName("Available Blocks")
                .setDisplayItem(Material.LEATHER)
                .setDescription("Nick was here")
                .setAvailability(CorePlayer::isInBuildWorld)
                .createLinkedContainer("TODO");
        hotbarItem.getLinkedChest()
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    BuildWorld buildWorld = BuildWorld.getPlayerBuildWorld(cp);
                    BuildWorldPlayer bwp = buildWorld.getPlayerMap().get(cp.getUniqueId());
                });
    }
    
}
