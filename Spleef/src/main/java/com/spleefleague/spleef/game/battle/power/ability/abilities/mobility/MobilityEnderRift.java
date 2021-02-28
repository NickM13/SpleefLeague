package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 5/29/2020
 */
public class MobilityEnderRift extends AbilityMobility {

    public static AbilityStats init() {
        return init(MobilityEnderRift.class)
                .setCustomModelData(4)
                .setName("Ender Rift")
                .setDescription("Return to your location up to %REVERSE_TIME% seconds in the past. May be reactivated to stop travel early.")
                .setUsage(15D);
    }

    private static final double REVERSE_TIME = 2D;
    private static final int REVERSE_SPEED = 10;

    private List<Location> riftLocs = new ArrayList<>();
    private boolean rifting = false;
    private int riftSkip = 0;

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {
        if (rifting) {
            if (riftLocs.isEmpty()) {
                stopRifting();
            } else {
                Location loc = riftLocs.remove(riftLocs.size() - 1);

                getUser().getBattle().getGameWorld().setTempBlock(new BlockPosition(loc.toVector()).add(new BlockPosition(-1, -1, 0)), Material.SNOW_BLOCK.createBlockData(), 30, true);
                getUser().getBattle().getGameWorld().setTempBlock(new BlockPosition(loc.toVector()).add(new BlockPosition(0, -1, -1)), Material.SNOW_BLOCK.createBlockData(), 30, true);
                getUser().getBattle().getGameWorld().setTempBlock(new BlockPosition(loc.toVector()).add(new BlockPosition(1, -1, 0)), Material.SNOW_BLOCK.createBlockData(), 30, true);
                getUser().getBattle().getGameWorld().setTempBlock(new BlockPosition(loc.toVector()).add(new BlockPosition(0, -1, 1)), Material.SNOW_BLOCK.createBlockData(), 30, true);
                getUser().getBattle().getGameWorld().setTempBlock(new BlockPosition(loc.toVector()).add(new BlockPosition(0, -1, 0)), Material.SNOW_BLOCK.createBlockData(), 30, true);

                getPlayer().teleport(loc.setDirection(getPlayer().getLocation().getDirection()));
                getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.5f, 1.7f);
                GameUtils.spawnPlayerParticles(getUser(), getStats().getType().getDustMedium(), 1);
            }
        } else {
            if (riftSkip <= 0) {
                riftSkip = REVERSE_SPEED;
                riftLocs.add(getPlayer().getLocation().clone());
                if (riftLocs.size() > REVERSE_TIME * 20 / REVERSE_SPEED) {
                    riftLocs.remove(0);
                }
            } else {
                riftSkip--;
            }
        }
    }

    private void stopRifting() {
        getPlayer().setGravity(true);
        rifting = false;
        applyCooldown();
    }

    /**
     * Returns a percent number of how much is remaining until the value is fully charged
     *
     * @return
     */
    @Override
    protected double getMissingPercent() {
        if (rifting) {
            return 1.f - riftLocs.size() / (REVERSE_TIME * 20 / REVERSE_SPEED);
        }
        return super.getMissingPercent();
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        if (rifting) {
            stopRifting();
        } else {
            getPlayer().setGravity(false);
            rifting = true;
        }
        return false;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        rifting = false;
        riftLocs.clear();
        riftSkip = 0;
        getPlayer().setGravity(true);
    }

}
