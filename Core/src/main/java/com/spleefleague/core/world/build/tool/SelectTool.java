package com.spleefleague.core.world.build.tool;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.core.world.build.BuildWorldPlayer;
import com.spleefleague.core.world.build.BuildTool;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class SelectTool extends BuildTool {
    
    public SelectTool() {
        super((InventoryMenuItemHotbar) InventoryMenuAPI
                .createItemHotbar(8, "BUILD_TOOL_PLACEABLES")
                .setName("Available Blocks")
                .setDisplayItem(Material.LEATHER)
                .setDescription("Nick was here")
                .createLinkedContainer("Available Blocks"));
        getHotbarItem()
                .setAction(cp -> { cp.setInventoryMenuContainer(getHotbarItem().getLinkedContainer()); });
        getHotbarItem().getLinkedContainer().setOpenAction((container, cp) -> {
            container.clearUnsorted();
            BuildWorld buildWorld = BuildWorld.getPlayerBuildWorld(cp);
            BuildWorldPlayer bwp = (BuildWorldPlayer) buildWorld.getPlayerMap().get(cp.getUniqueId());
            for (Material mat : buildWorld.getBuildMaterials()) {
                ItemStack itemStack = new ItemStack(mat);
                if (itemStack.getItemMeta() != null) {
                    container.addMenuItem(InventoryMenuAPI.createItem()
                            .setName(itemStack.getItemMeta().getDisplayName())
                            .setDescription("Click to Receive")
                            .setDisplayItem(mat)
                            .setAction(cp2 -> cp2.getPlayer().getInventory().addItem(itemStack))
                            .setCloseOnAction(false));
                }
            }
        });
    }
    
    @Override
    public void use(CorePlayer cp, BuildWorld buildWorld) {
    
    }
}
