package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.AbilityUtils;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveLivingBomb extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveLivingBomb.class)
                .setCustomModelData(6)
                .setName("Living Bomb")
                .setDescription("Ignite a living bomb inside yourself, detonating after %DETONATE_AFTER% seconds, firing destructive shrapnel, destroying blocks in a small radius around the player and shooting them upwards. Players caught by the blast are knocked back.")
                .setUsage(15);
    }

    private static ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.lifeTicks = 100;
        projectileStats.fireRange = 3D;
        projectileStats.count = 40;
        projectileStats.hSpread = 180;
        projectileStats.vSpread = 30;
        projectileStats.collidable = false;
        projectileStats.bounces = 1;
        //projectileStats.hitKnockback = 1D;
        projectileStats.customModelDatas = Lists.newArrayList(1);
    }

    private static final int TICK_COUNT = 6;
    private static final double TICK_DELAY = 0.25D;
    private static final double EXPLODE_PERCENT = 0.75D;
    private static final double EXPLODE_RADIUS = 3;
    private static final double KNOCKBACK = 1.5;
    private static final double DETONATE_AFTER = TICK_COUNT * TICK_DELAY;

    private boolean bombing = false;

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {

    }

    private void tick(int count) {
        if (!bombing) return;
        if (count <= 0) {
            getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            getUser().getBattle().getGameWorld().breakBlocks(new BlockPosition(
                            getPlayer().getLocation().getBlockX(),
                            getPlayer().getLocation().getBlockY(),
                            getPlayer().getLocation().getBlockZ()),
                    1D, EXPLODE_RADIUS, EXPLODE_PERCENT);
            GameUtils.spawnPlayerParticles(getUser(), getStats().getType().getDustBig(), 2);
            for (BattlePlayer bp : getUser().getBattle().getBattlers()) {
                if (!bp.getCorePlayer().equals(getUser().getCorePlayer())) {
                    Vector direction = bp.getPlayer().getLocation().toVector().subtract(getPlayer().getLocation().toVector());
                    if (direction.length() < 4) {
                        CoreUtils.knockbackEntity(bp.getPlayer(), direction, KNOCKBACK);
                    }
                }
            }
            Location loc = getPlayer().getLocation().clone();
            loc.setPitch(0);
            loc.add(0, 1.2, 0);
            getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), loc, projectileStats);
            AbilityUtils.breakAbove(getUser(), 3);
            AbilityUtils.startFling(getUser(), new Vector(0, 1.2, 0), 0.2);
            bombing = false;
            return;
        }
        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
        getUser().getBattle().getGameWorld().runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> tick(count-1), (int) (TICK_DELAY * 20)));
        GameUtils.spawnPlayerParticles(getUser(), getStats().getType().getDustMedium(), 1);
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        bombing = true;
        tick(TICK_COUNT);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        bombing = false;
    }

}
