package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.AbilityUtils;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveLivingBomb extends AbilityOffensive {

    private static final int TICK_COUNT = 6;
    private static final double TICK_DELAY = 0.25D;
    private static final double EXPLODE_PERCENT = 0.75D;
    private static final double EXPLODE_RADIUS = 3;
    private static final double KNOCKBACK = 1.5;

    public OffensiveLivingBomb() {
        super(5, 1, 15, 0.25D);
    }

    @Override
    public String getDisplayName() {
        return "Living Bomb";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Ignite a living bomb inside yourself, detonating after " +
                Chat.STAT + TICK_COUNT * TICK_DELAY +
                Chat.DESCRIPTION + " seconds, firing destructive shrapnel, destroying blocks in a small radius around the player and shooting them upwards. Players caught by the blast are knocked back.";
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     *
     * @param psp
     */
    @Override
    public void update(PowerSpleefPlayer psp) {

    }

    private void tick(PowerSpleefPlayer psp, int count) {
        if (count <= 0) {
            psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            psp.getBattle().getGameWorld().breakBlocks(new BlockPosition(
                            psp.getPlayer().getLocation().getBlockX(),
                            psp.getPlayer().getLocation().getBlockY(),
                            psp.getPlayer().getLocation().getBlockZ()),
                    1D, EXPLODE_RADIUS, EXPLODE_PERCENT);
            GameUtils.spawnPlayerParticles(psp, getType().getDustBig(), 2);
            for (BattlePlayer bp : psp.getBattle().getBattlers()) {
                if (!bp.getCorePlayer().equals(psp.getCorePlayer())) {
                    Vector direction = bp.getPlayer().getLocation().toVector().subtract(psp.getPlayer().getLocation().toVector());
                    if (direction.length() < 4) {
                        CoreUtils.knockbackEntity(bp.getPlayer(), direction, KNOCKBACK);
                    }
                }
            }
            AbilityUtils.startFling(psp, new Vector(0, 1.5, 0), 0.2);
            psp.getPowerValueMap().put("livingbomb", false);
            return;
        }
        psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
        psp.getPowerValueMap().put("livingbomb", true);
        psp.getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> tick(psp, count-1), (int) (TICK_DELAY * 20)));
        GameUtils.spawnPlayerParticles(psp, getType().getDustMedium(), 1);
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        psp.getPowerValueMap().put("livingbomb", true);
        tick(psp, TICK_COUNT);
        return true;
    }

    /**
     * Called at the start of a round
     *
     * @param psp
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {
        psp.getPowerValueMap().put("livingbomb", false);
    }

}
