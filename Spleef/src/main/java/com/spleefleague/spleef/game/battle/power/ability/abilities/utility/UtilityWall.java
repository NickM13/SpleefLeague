package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilityWall extends AbilityUtility {

    private static final double DURATION = 3;

    public UtilityWall() {
        super(6, 15);
    }

    @Override
    public String getDisplayName() {
        return "Wall";
    }

    @Override
    public String getDescription() {
        return "Raise a destructible wall of snow from the ground at a target location.";
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        BuildStructure structure = BuildStructures.get("power:wall");
        GameWorld gameWorld = psp.getBattle().getGameWorld();
        Map<BlockPosition, FakeBlock> blocks = gameWorld.getFakeBlocks();
        World world = gameWorld.getWorld();
        Iterator<BlockRaycastResult> results = new Point(psp.getPlayer().getEyeLocation()).castBlocks(psp.getPlayer().getLocation().getDirection(), 5).iterator();
        while (results.hasNext()) {
            BlockRaycastResult result = results.next();
            if ((blocks.containsKey(result.getBlockPos()) && !blocks.get(result.getBlockPos()).getBlockData().getMaterial().isAir()) ||
                    !world.getBlockAt(result.getBlockPos().getX(), result.getBlockPos().getY(), result.getBlockPos().getZ()).getType().isAir()) {
                Set<BlockPosition> changedBlocks = gameWorld.replaceAir(FakeUtils.translateBlocks(FakeUtils.rotateBlocks(
                        structure.getFakeBlocks(), ((int) (psp.getPlayer().getLocation().getYaw() + 45 / 4 + 90) / 45) * 45),
                        result.getBlockPos()));
                return true;
            }
            Random rand = new Random();
            if (!results.hasNext()) {
                for (BlockPosition pos : gameWorld.replaceAir(FakeUtils.translateBlocks(FakeUtils.rotateBlocks(
                        structure.getFakeBlocks(), ((int) (psp.getPlayer().getLocation().getYaw() + 45 / 2 + 90) / 45) * 45),
                        result.getBlockPos()))) {
                    gameWorld.setBlockDelayed(pos, Material.AIR.createBlockData(), rand.nextInt() % 20 + 30);
                }
                psp.getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                        result.getBlockPos().getX() + 0.5,
                        result.getBlockPos().getY() + 0.5,
                        result.getBlockPos().getZ() + 0.5,
                        30, 1.0, 0.5, 1.0, 0D, getType().getDustBig());
                return true;
            }
        }
        return false;
    }

    /**
     * Called at the start of a round
     *
     * @param psp Casting Player
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {

    }

}
