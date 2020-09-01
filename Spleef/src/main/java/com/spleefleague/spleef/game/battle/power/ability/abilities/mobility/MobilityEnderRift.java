package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 5/29/2020
 */
public class MobilityEnderRift extends AbilityMobility {

    private static final double REVERSE_TIME = 5D;
    private static final int REVERSE_SPEED = 10;

    public MobilityEnderRift() {
        super(4, 10);
    }

    @Override
    public String getDisplayName() {
        return "Ender Rift";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Return to your location up to " +
                Chat.STAT + REVERSE_TIME +
                Chat.DESCRIPTION + " seconds in the past. May be reactivated to stop travel early.";
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     *
     * @param psp
     */
    @Override
    public void update(PowerSpleefPlayer psp) {
        List<Location> pastLocs = (ArrayList<Location>) psp.getPowerValueMap().get("enderrift");
        if ((boolean) psp.getPowerValueMap().get("enderrifting")) {
            if (pastLocs.isEmpty()) {
                stopRifting(psp);
            } else {
                psp.getPlayer().teleport(pastLocs.remove(pastLocs.size() - 1).setDirection(psp.getPlayer().getLocation().getDirection()));
                psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.5f, 1.7f);
                GameUtils.spawnPlayerParticles(psp, getType().getDustMedium(), 1);
            }
        } else {
            if ((int) psp.getPowerValueMap().get("riftskip") <= 0) {
                psp.getPowerValueMap().put("riftskip", REVERSE_SPEED);
                pastLocs.add(psp.getPlayer().getLocation());
                if (pastLocs.size() > REVERSE_TIME * 20 / REVERSE_SPEED) {
                    pastLocs.remove(0);
                }
            } else {
                psp.getPowerValueMap().put("riftskip", (int) psp.getPowerValueMap().get("riftskip") - 1);
            }
        }
    }

    private void stopRifting(PowerSpleefPlayer psp) {
        psp.getPlayer().setGravity(true);
        psp.getPowerValueMap().put("enderrifting", false);
        applyCooldown(psp);
    }

    /**
     * Returns a percent number of how much is remaining until the value is fully charged
     *
     * @param psp Casting Player
     * @return
     */
    @Override
    protected double getMissingPercent(PowerSpleefPlayer psp) {
        if ((boolean) psp.getPowerValueMap().get("enderrifting")) {
            return 1.f - ((ArrayList<Location>) psp.getPowerValueMap().get("enderrift")).size() / (REVERSE_TIME * 20 / REVERSE_SPEED);
        }
        return super.getMissingPercent(psp);
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        if ((boolean) psp.getPowerValueMap().get("enderrifting")) {
            stopRifting(psp);
        } else {
            psp.getPlayer().setGravity(false);
            psp.getPowerValueMap().put("enderrifting", true);
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
        psp.getPowerValueMap().put("enderrifting", false);
        psp.getPowerValueMap().put("enderrift", new ArrayList<Location>());
        psp.getPowerValueMap().put("riftskip", 0);
        psp.getPlayer().setGravity(true);
    }

}
