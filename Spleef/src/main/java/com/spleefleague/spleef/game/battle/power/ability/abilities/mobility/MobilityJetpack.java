package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/22/2020
 */
public class MobilityJetpack extends AbilityMobility {

    private static final double MAX_FUEL = 100;
    private static final double CONSUME_FLY = 2;
    private static final double CONSUME_FALL = 1;
    private static final double REFUEL_RATE = 3.5;
    private static final double UP_POWER = 0.2;
    private static final double UP_CAP = 0.35;
    private static final double DOWN_POWER = 0.2;
    private static final double DOWN_CAP = 0.25;
    private static final double HEIGHT_CAP = 4.5D;

    public MobilityJetpack() {
        super(3, 10);
    }

    @Override
    public String getDisplayName() {
        return "Jetpack";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Blast off to the skies, allowing flight in the direction of your mouse cursor for up to " +
                Chat.STAT + MAX_FUEL / (CONSUME_FLY * 20) +
                Chat.DESCRIPTION + " seconds. May be reactivated to cancel early.";
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     *
     * @param psp
     */
    @Override
    public void update(PowerSpleefPlayer psp) {
        double fuel = (double) psp.getPowerValueMap().get("fuel");
        double jetpackStart = (double) psp.getPowerValueMap().get("jetpacking");
        if (jetpackStart >= 0 && jetpackStart < psp.getBattle().getRoundTime()) {
            if (fuel <= 0) {
                disableJetpack(psp);
            } else {
                if (psp.getPlayer().isSneaking()) {
                    psp.getPlayer().setVelocity(psp.getPlayer().getVelocity().setY(Math.max(psp.getPlayer().getVelocity().getY() - DOWN_POWER, -DOWN_CAP)));
                    fuel = Math.max(0, fuel - CONSUME_FALL);
                    if (FakeUtils.isOnGround(psp.getCorePlayer())) {
                        disableJetpack(psp);
                    } else {
                        psp.getPlayer().setVelocity(psp.getPlayer().getVelocity().add(psp.getPlayer().getLocation().getDirection().setY(0).multiply(0.1D)));
                    }
                } else {
                    BlockPosition pos = FakeUtils.getHighestBlockBelow(psp.getCorePlayer());
                    psp.getPlayer().setVelocity(psp.getPlayer().getVelocity().add(psp.getPlayer().getLocation().getDirection().setY(0).multiply(0.1D)));
                    if (psp.getPlayer().getLocation().getY() - pos.getY() <= HEIGHT_CAP + 1) {
                        psp.getPlayer().setVelocity(psp.getPlayer().getVelocity().setY(Math.min(UP_CAP, psp.getPlayer().getVelocity().getY() + UP_POWER)));
                        fuel = Math.max(0, fuel - CONSUME_FLY);
                    } else {
                        psp.getPlayer().setVelocity(psp.getPlayer().getVelocity().setY(Math.max(psp.getPlayer().getVelocity().getY() - DOWN_POWER / 2, -DOWN_CAP)));
                        fuel = Math.max(0, fuel - CONSUME_FLY / 2);
                    }
                }
                psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.ENTITY_CAT_HISS, 0.1f, 0.7f);
                psp.getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                        psp.getPlayer().getLocation().getX(),
                        psp.getPlayer().getLocation().getY(),
                        psp.getPlayer().getLocation().getZ(),
                        3, 0.1, 0.1, 0.1, 0D, getType().getDustMedium());
            }
        } else if (isReady(psp)) {
            fuel = Math.min(MAX_FUEL, fuel + REFUEL_RATE);
        }
        psp.getPowerValueMap().put("fuel", fuel);
    }

    /**
     * Returns a percent number of how much is remaining until the value is fully charged
     *
     * @param psp Casting Player
     * @return Missing Percent (0 to 1)
     */
    @Override
    protected double getMissingPercent(PowerSpleefPlayer psp) {
        return 1. - (double) psp.getPowerValueMap().get("fuel") / MAX_FUEL;
    }

    private void disableJetpack(PowerSpleefPlayer psp) {
        psp.getPowerValueMap().put("jetpacking", -1D);
        psp.getPlayer().setGravity(true);
        applyCooldown(psp);
    }

    private void enableJetpack(PowerSpleefPlayer psp) {
        psp.getPlayer().setGravity(false);
        psp.getPowerValueMap().put("jetpacking", psp.getBattle().getRoundTime());
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        if ((double) psp.getPowerValueMap().get("jetpacking") >= 0D) {
            disableJetpack(psp);
        } else {
            if ((double) psp.getPowerValueMap().get("fuel") > 0) {
                enableJetpack(psp);
                if (FakeUtils.isOnGround(psp.getCorePlayer())) {
                    psp.getPlayer().setVelocity(psp.getPlayer().getLocation().getDirection().multiply(0.25).add(new Vector(0, 0.35D, 0)));
                    psp.getPowerValueMap().put("fuel", Math.max(0D, (double) psp.getPowerValueMap().get("fuel") - 5));
                    psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
                } else {
                    psp.getPlayer().setVelocity(psp.getPlayer().getVelocity().setY(Math.max(-DOWN_CAP, psp.getPlayer().getVelocity().getY())));
                }
            }
        }
        return false;
    }

    /**
     * This is called when a player tried to use an ability while it's on cooldown, used for
     * re-activatable abilities.
     *
     * @param psp Casting Player
     */
    @Override
    protected void onUseCooling(PowerSpleefPlayer psp) {
        if ((double) psp.getPowerValueMap().get("jetpacking") >= 0D) {
            disableJetpack(psp);
        }
    }

    @Override
    public void onHit(PowerSpleefPlayer psp) {
        if ((double) psp.getPowerValueMap().get("jetpacking") >= 0D) {
            disableJetpack(psp);
        }
    }

    /**
     * This is called when a  player starts sneaking
     *
     * @param psp
     */
    @Override
    public void onStartSneak(PowerSpleefPlayer psp) {
        super.onStartSneak(psp);
    }

    /**
     * Called at the start of a round
     *
     * @param psp
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {
        //disableJetpack(psp);
        psp.getPowerValueMap().put("jetpacking", -1D);
        psp.getPowerValueMap().put("fuel", MAX_FUEL);
        psp.getPlayer().setGravity(true);
    }

}
