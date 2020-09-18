package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.util.variable.EntityRaycastResult;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import com.spleefleague.spleef.game.battle.power.ability.abilities.mobility.MobilityHookshot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensivePunch extends AbilityOffensive {

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
        projectileStats.customModelData = 1;
    }

    public OffensivePunch() {
        super(7, 1, 10, 0.25D);
    }

    @Override
    public String getDisplayName() {
        return "Punch";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Empower your next punch for " +
                Chat.STAT + REMAIN +
                Chat.DESCRIPTION + " seconds, heavily knocking players back on impact.";
    }

    /**
     * Returns a percent number of how much is remaining until the value is fully charged
     *
     * @param psp Casting Player
     * @return
     */
    @Override
    protected double getMissingPercent(PowerSpleefPlayer psp) {
        double time = (double) psp.getPowerValueMap().get("punchtime");
        if (time >= 0) {
            return 1.f - (time - psp.getBattle().getRoundTime()) / REMAIN;
        } else {
            return super.getMissingPercent(psp);
        }
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     *
     * @param psp
     */
    @Override
    public void update(PowerSpleefPlayer psp) {
        double time = (double) psp.getPowerValueMap().get("punchtime");
        if (time >= 0) {
            if (time <= psp.getBattle().getRoundTime()) {
                deactivatePunch(psp);
                psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 1.5f);
            } else {
                Location handLocation = psp.getPlayer().getEyeLocation().clone()
                        .add(psp.getPlayer().getLocation().getDirection()
                                .crossProduct(new Vector(0, 1, 0)).normalize()
                                .multiply(0.35).add(new Vector(0, -0.75, 0)));
                psp.getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                        handLocation.getX(),
                        handLocation.getY(),
                        handLocation.getZ(),
                        1, 0D, 0D, 0D, 0D, Type.OFFENSIVE.getDustSmall());
            }
        }
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        if ((double) psp.getPowerValueMap().get("punchtime") < 0) {
            activatePunch(psp);
        }
        return false;
    }

    /**
     * Called at the start of a round
     *
     * @param psp
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {
        psp.getPowerValueMap().put("punchtime", -1D);
    }

    private void activatePunch(PowerSpleefPlayer psp) {
        psp.getPowerValueMap().put("punchtime", psp.getBattle().getRoundTime() + REMAIN);
        psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1.5f);
    }

    private void deactivatePunch(PowerSpleefPlayer psp) {
        psp.getPowerValueMap().put("punchtime", -1D);
        applyCooldown(psp);
    }

    /**
     * This is called when a  player starts sneaking
     *
     * @param psp
     * @param target
     */
    @Override
    public void onPlayerPunch(PowerSpleefPlayer psp, PowerSpleefPlayer target) {
        double time = (double) psp.getPowerValueMap().get("punchtime");
        if (time >= 0 && time > psp.getBattle().getRoundTime()) {
            CoreUtils.knockbackEntity(target.getPlayer(), psp.getPlayer().getLocation().getDirection(), POWER);
            deactivatePunch(psp);
            psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.ENTITY_BEE_STING, 1, 1);
            Location loc = target.getPlayer().getEyeLocation().clone();
            loc.setYaw(psp.getPlayer().getLocation().getYaw());
            loc.setPitch(30);
            psp.getBattle().getGameWorld().shootProjectile(psp.getCorePlayer(), loc, projectileStats);
        }
    }

}
