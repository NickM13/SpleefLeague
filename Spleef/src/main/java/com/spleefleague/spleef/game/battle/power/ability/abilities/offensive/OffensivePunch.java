package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.google.common.collect.Lists;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensivePunch extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensivePunch.class)
                .setCustomModelData(8)
                .setName("Punch")
                .setDescription("Empowers your next punch for %REMAIN% seconds, heavily knocking players back on impact")
                .setUsage(10);
    }

    private static final double REMAIN = 6;
    private static final double POWER = 2;

    private static final ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.lifeTicks = 10;
        projectileStats.fireRange = 5D;
        projectileStats.collidable = false;
        projectileStats.noClip = true;
        projectileStats.count = 20;
        projectileStats.hSpread = 90;
        projectileStats.vSpread = 60;
        projectileStats.customModelDatas = Lists.newArrayList(30);
    }

    private double punchTime = -1;

    /**
     * Returns a percent number of how much is remaining until the value is fully charged
     *
     * @return percent
     */
    @Override
    protected double getMissingPercent() {
        if (punchTime >= 0) {
            return 1.f - (punchTime - getUser().getBattle().getRoundTime()) / REMAIN;
        } else {
            return super.getMissingPercent();
        }
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {
        if (punchTime >= 0) {
            if (punchTime <= getUser().getBattle().getRoundTime()) {
                deactivatePunch();
                getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 1.5f);
            } else {
                Location handLocation = getPlayer().getEyeLocation().clone()
                        .add(getPlayer().getLocation().getDirection()
                                .crossProduct(new Vector(0, 1, 0)).normalize()
                                .multiply(0.35).add(new Vector(0, -0.75, 0)));
                getUser().getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                        handLocation.getX(),
                        handLocation.getY(),
                        handLocation.getZ(),
                        1, 0D, 0D, 0D, 0D, Type.OFFENSIVE.getDustSmall());
            }
        }
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        if (punchTime < 0) {
            activatePunch();
        }
        return false;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        punchTime = -1;
    }

    private void activatePunch() {
        punchTime = getUser().getBattle().getRoundTime() + REMAIN;
        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1.5f);
    }

    private void deactivatePunch() {
        punchTime = -1;
        applyCooldown();
    }

    /**
     * This is called when a  player starts sneaking
     *
     * @param target
     */
    @Override
    public void onPlayerPunch(PowerSpleefPlayer target) {
        if (punchTime >= 0 && punchTime > getUser().getBattle().getRoundTime()) {
            CoreUtils.knockbackEntity(target.getPlayer(), getPlayer().getLocation().getDirection(), POWER);
            deactivatePunch();
            getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_BEE_STING, 1, 1);
            Location loc = target.getPlayer().getEyeLocation().clone();
            loc.setYaw(getPlayer().getLocation().getYaw());
            loc.setPitch(30);
            getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), loc, projectileStats);
        }
    }

}
