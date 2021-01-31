package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.google.common.collect.Lists;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveBouncingBomb extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveBouncingBomb.class)
                .setCustomModelData(3)
                .setName("Bouncing Bomb")
                .setDescription("Throw a bouncing bomb forward, destroying blocks in a small radius around its impact point. The bomb may bounce off of surrounding blocks and players up to %BOUNCES% times.")
                .setUsage(10);
    }

    private static final ProjectileStats bombStats = new ProjectileStats();

    public static class BouncingProjectile extends FakeEntitySnowball {

        public BouncingProjectile(ProjectileWorld projectileWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
            super(projectileWorld, shooter, location, projectileStats, charge);
        }

        @Override
        public void tick() {
            super.tick();
        }

        @Override
        protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            if (super.onBlockHit(craftEntity, blockRaycastResult)) {
                projectileWorld.spawnParticles(Particle.REDSTONE,
                        blockRaycastResult.getIntersection().getX(),
                        blockRaycastResult.getIntersection().getY(),
                        blockRaycastResult.getIntersection().getZ(),
                        40, 1, 1, 1, 0D, Type.OFFENSIVE.getDustMedium());
                projectileWorld.playSound(blockRaycastResult.getIntersection().toLocation(projectileWorld.getWorld()), Sound.ENTITY_LLAMA_SPIT, 1, 1.5f);
                return true;
            }
            return false;
        }

    }

    private static int BOUNCES = 2;

    static {
        bombStats.entityClass = BouncingProjectile.class;
        bombStats.customModelDatas = Lists.newArrayList(13);
        bombStats.fireRange = 4D;
        bombStats.breakRadius = 2D;
        bombStats.noClip = true;
        bombStats.bounces = BOUNCES;
        bombStats.bounciness = 0.65D;
    }

    @Override
    public boolean onUse() {
        getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), bombStats);
        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.BLOCK_COMPOSTER_EMPTY, 1, 1);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
