package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
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

    public MobilityHeroicLeap() {
        super(5, 10);
    }

    @Override
    public String getDisplayName() {
        return "Heroic Leap";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Leap high into the air. Reactivate while air-bound to crash into a target location, destroying a small portion of nearby blocks.";
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     *
     * @param psp
     */
    @Override
    public void update(PowerSpleefPlayer psp) {
        if (psp.getPowerValue(Double.class, "heroicleaping", -1D) >= 0D) {
            Vector dropDir = psp.getPowerValue(Vector.class, "heroicdrop");
            if (FakeUtils.isOnGround(psp.getCorePlayer())) {
                psp.getPlayer().setVelocity(new Vector(0, 0, 0));
                psp.getPowerValueMap().put("heroicleaping", -1D);
                if (dropDir != null) {
                    psp.getPowerValueMap().put("heroicdrop", null);
                    psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                    Location originLoc = psp.getPlayer().getLocation().clone();
                    originLoc.setPitch(0);
                    originLoc.add(originLoc.getDirection().normalize().multiply(1));
                    BlockPosition origin = new BlockPosition(originLoc.getBlockX(), originLoc.getBlockY(), originLoc.getBlockZ());
                    psp.getBattle().getGameWorld().breakBlocks(new BlockPosition(psp.getPlayer().getLocation().getBlockX(),
                            psp.getPlayer().getLocation().getBlockY(),
                            psp.getPlayer().getLocation().getBlockZ()), 2, 3.2, 0.4);
                    //for (BlockPosition pos : FakeUtils.createCone(originLoc.getDirection(), 15, 15)) {
                        //psp.getBattle().getGameWorld().breakBlock(pos.add(origin), psp.getCorePlayer());
                        //psp.getBattle().getGameWorld().setBlock(pos.add(origin), Material.CYAN_CONCRETE_POWDER.createBlockData());
                    //}
                    psp.getPlayer().setSneaking(false);
                } else {
                    applyCooldown(psp);
                }
            } else if (dropDir != null) {
                psp.getPlayer().setVelocity(dropDir.multiply(1.2));
                psp.getPlayer().setSneaking(true);
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
        if ((double) psp.getPowerValueMap().get("heroicleaping") >= 0D && psp.getPowerValueMap().get("heroicdrop") == null) {
            AbilityUtils.stopFling(psp);
            Location dir = psp.getPlayer().getLocation().clone();
            dir.setPitch(Math.max(30, psp.getPlayer().getLocation().getPitch()));
            psp.getPowerValueMap().put("heroicdrop", dir.getDirection());
            return true;
        } else {
            psp.getPowerValueMap().put("heroicleaping", psp.getBattle().getRoundTime());
            psp.getPowerValueMap().put("heroicdrop", null);
            AbilityUtils.startFling(psp, new Vector(0, 1., 0), 0.5);
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
        psp.getPowerValueMap().put("heroicleaping", -1D);
        psp.getPowerValueMap().put("heroicdrop", null);
    }

}
