package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveRiptide extends AbilityOffensive {

    private static final double TIME = 0.75;

    public OffensiveRiptide() {
        super(8, 3);
    }

    @Override
    public String getDisplayName() {
        return "Riptide";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Quickly destroy " +
                Chat.STAT + "3" +
                Chat.DESCRIPTION + " progressively larger rings around the player, regenerating after " +
                Chat.STAT + "2" +
                Chat.DESCRIPTION + " seconds.";
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        BlockPosition blockPos = new BlockPosition(
                psp.getPlayer().getLocation().getBlockX(),
                psp.getPlayer().getLocation().getBlockY() - 1,
                psp.getPlayer().getLocation().getBlockZ());
        for (int i = 0; i < 3; i++) {
            Set<BlockPosition> blocks = FakeUtils.createCylinderShell(i * 2 + 1, 1);
            int j = i;
            psp.getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () ->
                    GameUtils.spawnRingParticles(
                            psp.getBattle().getGameWorld(),
                            blockPos.toVector().add(new Vector(0.5, 1.5, 0.5)),
                            Type.OFFENSIVE.getDustMedium(), j * TIME,
                            (int) Math.pow(10, j) + 5),
                            (int) ((i * TIME) * 20L)));
            for (BlockPosition pos : blocks) {
                psp.getBattle().getGameWorld().setBlockDelayed(pos.add(blockPos), Material.AIR.createBlockData(), (int) ((i * TIME) * 20L));
            }
        }
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
