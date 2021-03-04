package com.spleefleague.zone.gear.fortunescarabs;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.zone.gear.Gear;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
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
    }

    private static final BlockData goldBlockData = Material.GOLD_BLOCK.createBlockData();
    private static final BlockData sandBlockData = Material.SAND.createBlockData();

    private void startDrilling(CorePlayer corePlayer, BlockPosition pos) {
        Vector center = new Vector(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        GlobalWorld globalWorld = corePlayer.getGlobalWorld();
        Location spinningLoc = new Location(globalWorld.getWorld(), center.getX(), center.getY(), center.getZ(), 0, 70);
        globalWorld.addRepeatingTask(() -> {
            spinningLoc.setY(spinningLoc.getY() - 0.05);
            spinningLoc.setYaw(spinningLoc.getYaw() + 25);
            corePlayer.getPlayer().teleport(spinningLoc);
        }, 100, 1);
        globalWorld.addRepeatingTask(() -> {
            globalWorld.spawnParticles(Particle.BLOCK_CRACK,
                    center.getX(), center.getY(), center.getZ(),
                    25, 0.2, 1, 0.2, 1,
                    goldBlockData);
            globalWorld.spawnParticles(Particle.BLOCK_CRACK,
                    center.getX(), center.getY(), center.getZ(),
                    25, 0.2, 1, 0.2, 1,
                    sandBlockData);
            globalWorld.playSound(spinningLoc, Sound.BLOCK_SAND_HIT, 0.7f, 2, "Sound:Gear");
        }, 50, 2);
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
