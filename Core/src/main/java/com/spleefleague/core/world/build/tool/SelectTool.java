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
                .createItemHotbar(0, "BUILD_TOOL_SELECT")
                .setName("Select Material")
                .setDisplayItem(cp -> { return new ItemStack(((BuildWorldPlayer) BuildWorld.getPlayerBuildWorld(cp).getPlayerMap()).getSelectedMaterialDisplay()); })
                .setDescription("Nick was here")
                .createLinkedContainer("Select Material Menu"));
        getHotbarItem().getLinkedContainer().setOpenAction((container, cp) -> {
            container.clearUnsorted();
            BuildWorld buildWorld = BuildWorld.getPlayerBuildWorld(cp);
            BuildWorldPlayer bwp = (BuildWorldPlayer) buildWorld.getPlayerMap().get(cp.getUniqueId());
            for (Material mat : buildWorld.getBuildMaterials()) {
                ItemStack itemStack = new ItemStack(mat);
                container.addMenuItem(InventoryMenuAPI.createItem()
                        .setName(itemStack.getItemMeta().getDisplayName())
                        .setDescription("Sets build material to this")
                        .setDisplayItem(mat)
                        .setAction(cp2 -> {
                            cp2.sendMessage("Material set!");
                            bwp.setSelectedMaterial(mat);
                        }));
            }
        });
    }
    
    @Override
    public void use(CorePlayer cp, BuildWorld buildWorld) {
    
    }
}
