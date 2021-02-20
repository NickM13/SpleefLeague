package com.spleefleague.core.world.build;

import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.player.CorePlayer;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public abstract class BuildTool {

    private final InventoryMenuItemHotbar hotbarItem;

    public BuildTool(InventoryMenuItemHotbar hotbarItem) {
        this.hotbarItem = (InventoryMenuItemHotbar) hotbarItem
                .setAvailability(CorePlayer::isInBuildWorld);
    }

    public InventoryMenuItemHotbar getHotbarItem() {
        return hotbarItem;
    }

    public abstract void use(CoreOfflinePlayer cp, BuildWorld buildWorld);

}
