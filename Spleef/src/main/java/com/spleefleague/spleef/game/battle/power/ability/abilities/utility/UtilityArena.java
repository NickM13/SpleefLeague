package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilityArena extends AbilityUtility {

    public UtilityArena() {
        super(1, 15);
    }

    @Override
    public String getDisplayName() {
        return "Arena";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "A wall of snow quickly surrounds the caster, rapidly decaying after " +
                Chat.STAT + "5" +
                Chat.DESCRIPTION + " seconds.";
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        BlockPosition blockPos = FakeUtils.getHighestFakeBlockBelow(psp.getCorePlayer()).add(new BlockPosition(0, 1, 0));
        if (blockPos.getY() <= 1) {
            blockPos = new BlockPosition(blockPos.getX(), psp.getCorePlayer().getLocation().getBlockY(), blockPos.getZ());
        }
        Set<BlockPosition> blockPositions = FakeUtils.createCylinderShell(5, 4);
        Map<BlockPosition, FakeBlock> blocks = new HashMap<>();
        for (BlockPosition pos : blockPositions) {
            psp.getBattle().getGameWorld().setBlockDelayed(pos.add(blockPos), Material.SNOW_BLOCK.createBlockData(), (pos.getY() + 1) * 8L);
            psp.getBattle().getGameWorld().addBlockDelayed(pos.add(blockPos), Material.AIR.createBlockData(), 100L - pos.getY() * 5L);
        }
        psp.getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                psp.getPlayer().getLocation().getX(),
                psp.getPlayer().getLocation().getY() + 1,
                psp.getPlayer().getLocation().getZ(),
                30, 0.7, 2, 0.7, 0D, getType().getDustBig());
        return true;
    }

    /**
     * Called at the start of a round
     *
     * @param psp
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {

    }

}
