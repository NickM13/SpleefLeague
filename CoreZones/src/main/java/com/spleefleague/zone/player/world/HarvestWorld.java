package com.spleefleague.zone.player.world;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.zone.CoreZones;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * @author NickM13
 * @since 2/26/2021
 */
public class HarvestWorld extends GameWorld {

    private static HarvestWorld HARVEST_WORLD;

    public static void init() {
        HARVEST_WORLD = new HarvestWorld(Core.OVERWORLD);
    }

    public static HarvestWorld getGlobal() {
        return HARVEST_WORLD;
    }

    protected HarvestWorld(World world) {
        super(world);
    }

    @Override
    protected boolean onBlockPunch(CorePlayer cp, BlockPosition pos) {
        if (!cp.canBuild() && getFakeBlock(pos) == null && getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getType().equals(Material.GRASS)) {
            // Perhaps give something here!
            setBlockForced(pos, FakeWorld.AIR);
            runTask(Bukkit.getScheduler().runTaskLater(CoreZones.getInstance(), () -> {
                removeBlock(pos);
            }, 100L));
            return true;
        }
        return false;
    }

    @Override
    protected boolean onItemUse(CorePlayer cp, BlockPosition blockPosition, BlockPosition blockRelative) {
        return false;
    }
}
