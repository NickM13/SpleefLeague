package com.spleefleague.core.world.build.tool;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.util.variable.RaycastResult;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.core.world.build.BuildTool;
import org.bukkit.Material;

import java.util.List;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class BreakTool extends BuildTool {
    
    public BreakTool() {
        super((InventoryMenuItemHotbar) InventoryMenuAPI
                .createItemHotbar(0, "BUILD_TOOL_BREAK")
                .setName("Break Tool")
                .setDisplayItem(Material.BARRIER)
                .setDescription("Nick was here"));
    }
    
    @Override
    public void use(CorePlayer cp, BuildWorld buildWorld) {
        Point point = new Point(cp.getPlayer().getEyeLocation());
        List<RaycastResult> results = point.cast(cp.getPlayer().getEyeLocation().getDirection(), 20);
        FakeBlock fakeBlock;
        for (int i = 1; i < results.size(); i++) {
            fakeBlock = buildWorld.getFakeBlocks().get(results.get(i).blockPos);
            if (!results.get(i).blockPos.toLocation(buildWorld.getWorld()).getBlock().isEmpty()
                    || (fakeBlock != null && !fakeBlock.getBlockData().getMaterial().isAir())) {
                buildWorld.breakBlock(results.get(i).relative);
                break;
            }
        }
    }
}
