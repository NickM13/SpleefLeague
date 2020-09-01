package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/17/2020
 */
public class MobilityAirDash extends AbilityMobility {

    private static final float MAX_PITCH = -10;
    private static final float MIN_PITCH = -20;
    private static final float POWER = 1.3f;
    private static final float AIR_MULTIPLIER = 0.7f;

    public MobilityAirDash() {
        super(1, 3, 10D, 1D);
    }

    @Override
    public String getDisplayName() {
        return "Air Dash";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Dash a short distance forward. Holds up to " +
                Chat.STAT + this.charges +
                Chat.DESCRIPTION + " charges.";
    }

    private void tick(PowerSpleefPlayer psp, int count) {
        if (count <= 0) {
            return;
        }
        psp.getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> {
            tick(psp, count-1);
            psp.getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                    psp.getPlayer().getLocation().getX(),
                    psp.getPlayer().getLocation().getY() - 0.05,
                    psp.getPlayer().getLocation().getZ(),
                    5, 0.5, 0.1, 0.5, 0D, getType().getDustMedium());
        }, 2));
    }

    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        Location loc = psp.getPlayer().getLocation().clone();
        loc.setPitch(Math.max(MIN_PITCH, Math.min(MAX_PITCH, loc.getPitch())));
        Vector direction = loc.getDirection().multiply(POWER);
        if (!FakeUtils.isOnGround(psp.getCorePlayer())) {
            direction = direction.multiply(AIR_MULTIPLIER);
        }
        psp.getPlayer().setVelocity(direction);
        psp.getBattle().getGameWorld().playSound(loc, Sound.ENTITY_ZOMBIE_INFECT, 1.0f, 1.2f);
        tick(psp, 5);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {

    }

}
