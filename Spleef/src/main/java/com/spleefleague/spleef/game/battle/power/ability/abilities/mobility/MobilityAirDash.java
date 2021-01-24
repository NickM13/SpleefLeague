package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/17/2020
 */
public class MobilityAirDash extends AbilityMobility {

    public static AbilityStats init() {
        return init(MobilityAirDash.class)
                .setCustomModelData(2)
                .setName("Air Dash")
                .setDescription("Dash a short distance forward. Holds up to %charges% charges.")
                .setUsage(3, 10D, 1D);
    }

    private static final float MAX_PITCH = -10;
    private static final float MIN_PITCH = -20;
    private static final float POWER = 1.3f;
    private static final float AIR_MULTIPLIER = 0.7f;

    private int dustCount = 0;

    private void tick(int count) {
        if (count <= 0) {
            return;
        }
        getUser().getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> {
            tick(count-1);
            getUser().getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                    getPlayer().getLocation().getX(),
                    getPlayer().getLocation().getY() - 0.05,
                    getPlayer().getLocation().getZ(),
                    5, 0.5, 0.1, 0.5, 0D, getStats().getType().getDustMedium());
        }, 2));
    }

    @Override
    public boolean onUse() {
        Location loc = getPlayer().getLocation().clone();
        loc.setPitch(Math.max(MIN_PITCH, Math.min(MAX_PITCH, loc.getPitch())));
        Vector direction = loc.getDirection().multiply(POWER);
        if (!FakeUtils.isOnGround(getUser().getCorePlayer())) {
            direction = direction.multiply(AIR_MULTIPLIER);
        }
        getPlayer().setVelocity(direction);
        getUser().getBattle().getGameWorld().playSound(loc, Sound.ENTITY_ZOMBIE_INFECT, 1.0f, 1.2f);
        tick(5);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
