package com.spleefleague.core.world.build;

import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.build.BuildWorld;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public abstract class BuildTool {
    
    private final InventoryMenuItemHotbar hotbarItem;
    
    public BuildTool(InventoryMenuItemHotbar hotbarItem) {
        this.hotbarItem = hotbarItem;
    }
    
    public InventoryMenuItemHotbar getHotbarItem() {
        return hotbarItem;
    }
    
    public abstract void use(CorePlayer cp, BuildWorld buildWorld);
    
}
