package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.AbilityUtils;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/30/2020
 */
public class MobilityHeroicLeap extends AbilityMobility {

    public static AbilityStats init() {
        return init(MobilityHeroicLeap.class)
                .setCustomModelData(5)
                .setName("Heroic Leap")
                .setDescription("Leap high into the air. Reactivate while air-bound to crash into a target location, destroying a small portion of nearby blocks.")
                .setUsage(10D);
    }
    
    private double heroicLeaping = -1;
    private Vector heroicDrop = null;

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {
        if (heroicLeaping >= 0D) {
            Vector dropDir = heroicDrop;
            if (heroicLeaping > getUser().getBattle().getRoundTime() + 0.25 && FakeUtils.isOnGround(getUser().getCorePlayer())) {
                getPlayer().setVelocity(new Vector(0, 0, 0));
                heroicLeaping = -1;
                if (dropDir != null) {
                    heroicDrop = null;
                    getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    Location originLoc = getPlayer().getLocation().clone();
                    originLoc.setPitch(0);
                    originLoc.add(originLoc.getDirection().normalize().multiply(1));
                    BlockPosition origin = new BlockPosition(originLoc.getBlockX(), originLoc.getBlockY(), originLoc.getBlockZ());
                    getUser().getBattle().getGameWorld().breakBlocks(new BlockPosition(getPlayer().getLocation().getBlockX(),
                            getPlayer().getLocation().getBlockY(),
                            getPlayer().getLocation().getBlockZ()), 2, 3.2, 0.4);
                    //for (BlockPosition pos : FakeUtils.createCone(originLoc.getDirection(), 15, 15)) {
                        //psp.getBattle().getGameWorld().breakBlock(pos.add(origin), psp.getCorePlayer());
                        //psp.getBattle().getGameWorld().setBlock(pos.add(origin), Material.CYAN_CONCRETE_POWDER.createBlockData());
                    //}
                    getPlayer().setSneaking(false);
                } else {
                    applyCooldown();
                }
            } else if (dropDir != null) {
                getPlayer().setVelocity(dropDir.clone().multiply(3));
                getPlayer().setSneaking(true);
            }
        }
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        if (heroicLeaping >= 0D && heroicDrop == null) {
            AbilityUtils.stopFling(getUser());
            Location dir = getPlayer().getLocation().clone();
            dir.setPitch(Math.max(30, getPlayer().getLocation().getPitch()));
            heroicDrop = dir.getDirection();
            return true;
        } else {
            heroicLeaping = getUser().getBattle().getRoundTime();
            heroicDrop = null;
            AbilityUtils.startFling(getUser(), new Vector(0, 1., 0), 0.5);
        }
        return false;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        heroicLeaping = -1;
        heroicDrop = null;
    }

}
