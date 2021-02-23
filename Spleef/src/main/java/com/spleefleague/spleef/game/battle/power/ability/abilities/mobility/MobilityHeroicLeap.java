package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.AbilityUtils;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

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
    private Vector lastLoc;

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {
        if (heroicLeaping >= 0D) {
            if (!AbilityUtils.isFlinging(getUser())) {
                //if (FakeUtils.isOnGround(getUser().getCorePlayer()))
                Vector newLoc = getUser().getPlayer().getLocation().toVector();
                Vector diff = newLoc.clone().subtract(lastLoc);
                List<BlockRaycastResult> results = new ArrayList<>();
                results.addAll(new Point(lastLoc.clone().add(new Vector(0.35, 0, 0.35))).castBlocks(diff, diff.length() + 0.1));
                results.addAll(new Point(lastLoc.clone().add(new Vector(-0.35, 0, 0.35))).castBlocks(diff, diff.length() + 0.1));
                results.addAll(new Point(lastLoc.clone().add(new Vector(-0.35, 0, -0.35))).castBlocks(diff, diff.length() + 0.1));
                results.addAll(new Point(lastLoc.clone().add(new Vector(0.35, 0, -0.35))).castBlocks(diff, diff.length() + 0.1));
                GameWorld gameWorld = getUser().getBattle().getGameWorld();
                Point collide = null;
                for (BlockRaycastResult result : results) {
                    if (gameWorld.getFakeBlock(result.getBlockPos()) != null) {
                        collide = new Point(lastLoc.add(diff.multiply(result.getDistance())));
                        break;
                    }
                }
                lastLoc = newLoc;
                if (collide != null) {
                    if (newLoc.distance(new Vector(collide.getX(), collide.getY(), collide.getZ())) > 0.1) {
                        ((CraftPlayer) getPlayer()).getHandle().setPosition(collide.x, collide.y, collide.z);
                    }
                    getPlayer().setVelocity(new Vector(0, 0, 0));
                    heroicLeaping = -1;
                    if (heroicDrop != null) {
                        heroicDrop = null;
                        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        Location originLoc = getPlayer().getLocation().clone();
                        originLoc.setPitch(0);
                        originLoc.add(originLoc.getDirection().normalize().multiply(1));
                        getUser().getBattle().getGameWorld().breakBlocks(new BlockPosition(getPlayer().getLocation().getBlockX(),
                                getPlayer().getLocation().getBlockY(),
                                getPlayer().getLocation().getBlockZ()), 2, 3.2, 0.4);
                    } else {
                        applyCooldown();
                    }
                    return;
                }
            }
            if (heroicDrop != null) {
                getPlayer().setVelocity(heroicDrop.clone().multiply(2.5));
            }
        }
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        if (heroicLeaping >= 0D && heroicDrop == null) {
            if (!AbilityUtils.isFlinging(getUser())) {
                Location dir = getPlayer().getLocation().clone();
                dir.setPitch(Math.max(30, getPlayer().getLocation().getPitch()));
                heroicDrop = dir.getDirection();
                return true;
            }
        } else {
            lastLoc = getUser().getPlayer().getLocation().toVector().add(new Vector(0, 0.5, 0));
            heroicLeaping = getUser().getBattle().getRoundTime();
            heroicDrop = null;
            AbilityUtils.breakAbove(getUser(), 4);
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
