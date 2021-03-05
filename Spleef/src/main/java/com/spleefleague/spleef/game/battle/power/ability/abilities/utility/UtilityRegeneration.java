package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Bukkit;
import org.bukkit.Particle;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilityRegeneration extends AbilityUtility {

    public static AbilityStats init() {
        return init(UtilityRegeneration.class)
                .setCustomModelData(6)
                .setName("Regeneration")
                .setDescription("For %TOTAL% seconds, quickly regenerate the field and empty ground around the caster.")
                .setUsage(15);
    }

    private static final double RADIUS = 4D;
    private static final double SPACING = 0.25D;
    private static final int COUNT = 12;
    private static final double TOTAL = (SPACING * 20 * COUNT) / 20;

    private void regenDelay(int count) {
        if (count < 0) return;
        BlockPosition pos = new BlockPosition(
                getPlayer().getLocation().getBlockX(),
                getPlayer().getLocation().getBlockY() - 1,
                getPlayer().getLocation().getBlockZ());
        getUser().getBattle().getGameWorld().regenerateBlocks(pos, RADIUS);
        getUser().getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                getPlayer().getLocation().getX() - 0.35,
                getPlayer().getLocation().getY(),
                getPlayer().getLocation().getZ() - 0.35,
                30, 0.7, 1.8, 0.7, 0D, getStats().getType().getDustMedium());
        getUser().getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> regenDelay(count - 1), (int) (SPACING * 20)));
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {

    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        regenDelay(COUNT);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
