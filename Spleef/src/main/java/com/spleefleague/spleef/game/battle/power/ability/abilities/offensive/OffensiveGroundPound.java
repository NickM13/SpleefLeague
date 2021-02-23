package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.List;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveGroundPound extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveGroundPound.class)
                .setCustomModelData(4)
                .setName("Ground Pound")
                .setDescription("Slam the ground in front of you, rapidly decaying blocks in front of you over %X0.5% seconds.")
                .setUsage(10);
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        Location forward = getPlayer().getLocation().clone();
        forward.setPitch(0);
        Location forClone = forward.clone();
        forClone.add(forward.getDirection().multiply(2));
        BlockPosition blockPos = new BlockPosition(
                forClone.getBlockX(),
                forClone.getBlockY() - 1,
                forClone.getBlockZ());
        Set<BlockPosition> blocks = FakeUtils.createCone(forward.getDirection().clone(), 6, 5);
        GameUtils.spawnRingParticles(
                        getUser().getBattle().getGameWorld(),
                        getPlayer().getLocation().toVector(),
                        Type.OFFENSIVE.getDustMedium(), 1,
                        10);
        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        for (BlockPosition pos : blocks) {
            //getUser().getBattle().getGameWorld().setBlockDelayed(pos.add(blockPos), Material.AIR.createBlockData(), (long) pos.toVector().length() * 3 + 8);
        }
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
