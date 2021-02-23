package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilityWall extends AbilityUtility {

    public static AbilityStats init() {
        return init(UtilityWall.class)
                .setCustomModelData(11)
                .setName("Wall")
                .setDescription("Raise a destructible wall of snow from the ground at a target location.")
                .setUsage(15);
    }

    private static final double DURATION = 3;

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        BuildStructure structure = BuildStructures.get("power:wall");
        GameWorld gameWorld = getUser().getBattle().getGameWorld();
        World world = gameWorld.getWorld();
        Iterator<BlockRaycastResult> results = new Point(getPlayer().getEyeLocation()).castBlocks(getPlayer().getLocation().getDirection(), 5).iterator();
        while (results.hasNext()) {
            BlockRaycastResult result = results.next();
            FakeBlock fakeBlock = gameWorld.getFakeBlock(result.getBlockPos());
            if ((fakeBlock != null && !fakeBlock.getBlockData().getMaterial().isAir()) ||
                    !world.getBlockAt(result.getBlockPos().getX(), result.getBlockPos().getY(), result.getBlockPos().getZ()).getType().isAir()) {
                gameWorld.replaceAir(FakeUtils.translateBlocks(FakeUtils.rotateBlocks(
                        structure.getFakeBlocks(), Math.round(getPlayer().getLocation().getYaw() / 45) * 45),
                        result.getBlockPos()));
                return true;
            }
            Random rand = new Random();
            if (!results.hasNext()) {
                for (BlockPosition pos : gameWorld.replaceAir(FakeUtils.translateBlocks(FakeUtils.rotateBlocks(
                        structure.getFakeBlocks(), Math.round(getPlayer().getLocation().getYaw() / 45) * 45),
                        result.getBlockPos()))) {
                    gameWorld.setBlockDelayed(pos, FakeWorld.AIR, rand.nextInt() % 20 + 30);
                }
                getUser().getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                        result.getBlockPos().getX() + 0.5,
                        result.getBlockPos().getY() + 0.5,
                        result.getBlockPos().getZ() + 0.5,
                        30, 1.0, 0.5, 1.0, 0D, getStats().getType().getDustBig());
                return true;
            }
        }
        return false;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
