package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Map;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilitySafetyZone extends AbilityUtility {

    public static AbilityStats init() {
        return init(UtilitySafetyZone.class)
                .setCustomModelData(7)
                .setName("Safety Zone")
                .setDescription("For %DURATION% seconds, blocks around the caster are made invulnerable.")
                .setUsage(15);
    }

    private static final double RANGE = 4D;
    private static final double DURATION = 3D;

    private static final FakeBlock SAFE_BLOCK = new FakeBlock(Material.CYAN_CONCRETE.createBlockData());

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        Set<BlockPosition> blocks = FakeUtils.translateBlocks(FakeUtils.createSphere(RANGE), new BlockPosition(
                getPlayer().getLocation().getBlockX(),
                getPlayer().getLocation().getBlockY(),
                getPlayer().getLocation().getBlockZ()));
        GameWorld gameWorld = getUser().getBattle().getGameWorld();
        Map<BlockPosition, FakeBlock> changedBlocks = gameWorld.replaceBlocks(blocks, SAFE_BLOCK);
        getUser().getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> {
            for (Map.Entry<BlockPosition, FakeBlock> entry : changedBlocks.entrySet()) {
                gameWorld.setBlock(entry.getKey(), gameWorld.getBaseBlock(entry.getKey()));
            }
        }, (int) (DURATION * 20)));
        getUser().getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                getPlayer().getLocation().getX() - 0.35,
                getPlayer().getLocation().getY(),
                getPlayer().getLocation().getZ() - 0.35,
                30, 0.7, 1.8, 0.7, 0D, getStats().getType().getDustBig());
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
