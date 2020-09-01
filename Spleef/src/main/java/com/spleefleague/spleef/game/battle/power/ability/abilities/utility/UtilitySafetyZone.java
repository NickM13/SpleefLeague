package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Map;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilitySafetyZone extends AbilityUtility {

    private static final double RANGE = 4D;
    private static final double DURATION = 3D;

    public UtilitySafetyZone() {
        super(4, 20);
    }

    @Override
    public String getDisplayName() {
        return "Safety Zone";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "For " +
                Chat.STAT + DURATION +
                Chat.DESCRIPTION + " seconds blocks around the caster are made invulnerable.";
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        Set<BlockPosition> blocks = FakeUtils.translateBlocks(FakeUtils.createSphere(RANGE), new BlockPosition(
                psp.getPlayer().getLocation().getBlockX(),
                psp.getPlayer().getLocation().getBlockY(),
                psp.getPlayer().getLocation().getBlockZ()));
        GameWorld gameWorld = psp.getBattle().getGameWorld();
        Map<BlockPosition, FakeBlock> changedBlocks = gameWorld.replaceBlocks(blocks, Material.CYAN_CONCRETE.createBlockData());
        psp.getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> {
            for (Map.Entry<BlockPosition, FakeBlock> entry : changedBlocks.entrySet()) {
                gameWorld.setBlock(entry.getKey(), Material.SNOW_BLOCK.createBlockData(), true);
            }
            gameWorld.updateAll();
        }, (int) (DURATION * 20)));
        psp.getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                psp.getPlayer().getLocation().getX() - 0.35,
                psp.getPlayer().getLocation().getY(),
                psp.getPlayer().getLocation().getZ() - 0.35,
                30, 0.7, 1.8, 0.7, 0D, getType().getDustBig());
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
