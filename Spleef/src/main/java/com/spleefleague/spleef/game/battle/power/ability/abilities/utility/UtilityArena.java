package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilityArena extends AbilityUtility {

    public static AbilityStats init() {
        return init(UtilityArena.class)
                .setCustomModelData(1)
                .setName("Arena")
                .setDescription("A wall of snow quickly surrounds the caster, rapidly decaying after %X5% seconds.")
                .setUsage(15);
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        BlockPosition blockPos = FakeUtils.getHighestFakeBlockBelow(getUser().getCorePlayer()).add(new BlockPosition(0, 1, 0));
        if (blockPos.getY() <= 1) {
            blockPos = new BlockPosition(blockPos.getX(), getUser().getCorePlayer().getLocation().getBlockY(), blockPos.getZ());
        }
        Set<BlockPosition> blockPositions = FakeUtils.createCylinderShell(5, 4);
        Map<BlockPosition, FakeBlock> blocks = new HashMap<>();
        for (BlockPosition pos : blockPositions) {
            getUser().getBattle().getGameWorld().setBlockDelayed(pos.add(blockPos), Material.SNOW_BLOCK.createBlockData(), (pos.getY() + 1) * 8L);
            getUser().getBattle().getGameWorld().addBlockDelayed(pos.add(blockPos), Material.AIR.createBlockData(), 100L - pos.getY() * 5L);
        }
        getUser().getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                getPlayer().getLocation().getX(),
                getPlayer().getLocation().getY() + 1,
                getPlayer().getLocation().getZ(),
                30, 0.7, 2, 0.7, 0D, getStats().getType().getDustBig());
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
