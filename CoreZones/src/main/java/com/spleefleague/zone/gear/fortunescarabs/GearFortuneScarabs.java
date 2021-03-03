package com.spleefleague.zone.gear.fortunescarabs;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.zone.gear.Gear;
import org.bukkit.Material;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 3/1/2021
 */
public class GearFortuneScarabs extends Gear {

    private static final Material SCARAB_BLOCK = Material.CHISELED_STONE_BRICKS;

    public GearFortuneScarabs() {
        super(GearType.FORTUNE_SCARABS);
    }

    public GearFortuneScarabs(String identifier, String name) {
        super(GearType.FORTUNE_SCARABS, identifier, name);
        this.material = GearType.FORTUNE_SCARABS.material;
        this.customModelData = 1;
    }

    private void startDrilling(CorePlayer corePlayer, BlockPosition pos) {
        Vector center = new Vector(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        
    }

    @Override
    protected boolean onActivate(CorePlayer corePlayer) {
        GlobalWorld globalWorld = corePlayer.getGlobalWorld();
        for (BlockPosition pos : CoreUtils.getInsideBlocks(corePlayer.getPlayer().getBoundingBox().shift(0, -0.9, 0))) {
            if (globalWorld.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getType() == SCARAB_BLOCK) {
                startDrilling(corePlayer, pos);
                return true;
            }
        }
        return false;
    }

}
