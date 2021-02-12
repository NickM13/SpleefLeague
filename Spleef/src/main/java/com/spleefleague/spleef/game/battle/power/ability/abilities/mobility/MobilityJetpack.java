package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/22/2020
 */
public class MobilityJetpack extends AbilityMobility {

    public static AbilityStats init() {
        return init(MobilityJetpack.class)
                .setCustomModelData(8)
                .setName("Jetpack")
                .setDescription("Blast off to the skies, allowing flight in the direction of your mouse cursor for up to %MAX_FLY_TIME% seconds. May be reactivated to cancel early.")
                .setUsage(10);
    }

    private static final double MAX_FUEL = 100;
    private static final double CONSUME_FLY = 2;
    private static final double CONSUME_FALL = 1;
    private static final double REFUEL_RATE = 3.5;
    private static final double UP_POWER = 0.2;
    private static final double UP_CAP = 0.35;
    private static final double DOWN_POWER = 0.2;
    private static final double DOWN_CAP = 0.25;
    private static final double HEIGHT_CAP = 5.5D;

    private static final double MAX_FLY_TIME = MAX_FUEL / (CONSUME_FLY * 20);

    private double fuel;
    private double start;

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {
        if (start >= 0 && start < getUser().getBattle().getRoundTime()) {
            if (fuel <= 0) {
                disableJetpack();
            } else {
                if (getPlayer().isSneaking()) {
                    getPlayer().setVelocity(getPlayer().getVelocity().setY(Math.max(getPlayer().getVelocity().getY() - DOWN_POWER, -DOWN_CAP)));
                    fuel = Math.max(0, fuel - CONSUME_FALL);
                    if (FakeUtils.isOnGround(getUser().getCorePlayer())) {
                        disableJetpack();
                    } else {
                        getPlayer().setVelocity(getPlayer().getVelocity().add(getPlayer().getLocation().getDirection().setY(0).multiply(0.1D)));
                    }
                } else {
                    BlockPosition pos = FakeUtils.getHighestBlockBelow(getUser().getCorePlayer());
                    getPlayer().setVelocity(getPlayer().getVelocity().add(getPlayer().getLocation().getDirection().setY(0).multiply(0.1D)));
                    if (getPlayer().getLocation().getY() - pos.getY() <= HEIGHT_CAP + 1) {
                        getPlayer().setVelocity(getPlayer().getVelocity().setY(Math.min(UP_CAP, getPlayer().getVelocity().getY() + UP_POWER)));
                        fuel = Math.max(0, fuel - CONSUME_FLY);
                    } else {
                        getPlayer().setVelocity(getPlayer().getVelocity().setY(Math.max(getPlayer().getVelocity().getY() - DOWN_POWER / 4, -DOWN_CAP)));
                        fuel = Math.max(0, fuel - CONSUME_FLY / 2);
                    }
                }
                getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_CAT_HISS, 0.1f, 0.7f);
                getUser().getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                        getPlayer().getLocation().getX(),
                        getPlayer().getLocation().getY(),
                        getPlayer().getLocation().getZ(),
                        3, 0.1, 0.1, 0.1, 0D, getStats().getType().getDustMedium());
            }
        } else if (isReady()) {
            fuel = Math.min(MAX_FUEL, fuel + REFUEL_RATE);
        }
    }

    /**
     * Returns a percent number of how much is remaining until the value is fully charged
     *
     * @return Missing Percent (0 to 1)
     */
    @Override
    protected double getMissingPercent() {
        return 1. - fuel / MAX_FUEL;
    }

    private void disableJetpack() {
        start = -1;
        getPlayer().setGravity(true);
        applyCooldown();
    }

    private void enableJetpack() {
        getPlayer().setGravity(false);
        start = getUser().getBattle().getRoundTime();
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        if (start > 0) {
            disableJetpack();
        } else {
            if (fuel > 0) {
                enableJetpack();
                if (FakeUtils.isOnGround(getUser().getCorePlayer())) {
                    getPlayer().setVelocity(getPlayer().getLocation().getDirection().multiply(0.25).add(new Vector(0, 0.45D, 0)));
                    fuel -= 5;
                    getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
                } else {
                    getPlayer().setVelocity(getPlayer().getVelocity().setY(Math.max(-DOWN_CAP, getPlayer().getVelocity().getY()) + 0.2D));
                }
            }
        }
        return false;
    }

    /**
     * This is called when a player tried to use an ability while it's on cooldown, used for
     * re-activatable abilities.
     */
    @Override
    protected void onUseCooling() {
        if (start > 0) {
            disableJetpack();
        }
    }

    @Override
    public void onHit() {
        if (start > 0) {
            disableJetpack();
        }
    }

    /**
     * This is called when a  player starts sneaking
     */
    @Override
    public void onStartSneak() {
        super.onStartSneak();
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        //disableJetpack();
        start = -1;
        fuel = MAX_FUEL;
        getPlayer().setGravity(true);
    }

}
