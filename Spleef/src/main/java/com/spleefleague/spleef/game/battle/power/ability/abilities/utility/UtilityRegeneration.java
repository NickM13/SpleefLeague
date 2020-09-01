package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Bukkit;
import org.bukkit.Particle;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilityRegeneration extends AbilityUtility {

    private static final double RADIUS = 4D;
    private static final double SPACING = 0.25D;
    private static final int COUNT = 12;

    public UtilityRegeneration() {
        super(3, 15D);
    }

    @Override
    public String getDisplayName() {
        return "Regeneration";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "For " +
                Chat.STAT + SPACING * COUNT +
                Chat.DESCRIPTION + " seconds, quickly regenerate the field and empty ground around the caster.";
    }

    private void regenDelay(PowerSpleefPlayer psp, int count) {
        if (count < 0) return;
        BlockPosition pos = new BlockPosition(
                psp.getPlayer().getLocation().getBlockX(),
                psp.getPlayer().getLocation().getBlockY() - 1,
                psp.getPlayer().getLocation().getBlockZ());
        psp.getBattle().getGameWorld().regenerateBlocks(pos, RADIUS);
        psp.getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                psp.getPlayer().getLocation().getX() - 0.35,
                psp.getPlayer().getLocation().getY(),
                psp.getPlayer().getLocation().getZ() - 0.35,
                30, 0.7, 1.8, 0.7, 0D, getType().getDustMedium());
        psp.getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> regenDelay(psp, count - 1), (int) (SPACING * 20)));
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     *
     * @param psp
     */
    @Override
    public void update(PowerSpleefPlayer psp) {

    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        regenDelay(psp, COUNT);
        return true;
    }

    /**
     * Called at the start of a round
     *
     * @param psp
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {

    }

}
