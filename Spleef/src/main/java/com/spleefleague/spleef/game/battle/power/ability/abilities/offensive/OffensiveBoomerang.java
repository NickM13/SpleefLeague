package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/18/2020
 */
public class OffensiveBoomerang extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveBoomerang.class)
                .setCustomModelData(2)
                .setName("Boomerang")
                .setDescription("Throw a boomerang forward destroying all destructible blocks it passes, returning to the sender after %X1% second.")
                .setUsage(3);
    }

    public static class BoomerangProjectile extends FakeEntitySnowball {

        Vector targetDir = null;
        int distanceTravelled;

        public BoomerangProjectile(ProjectileWorld projectileWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
            super(projectileWorld, shooter, location, projectileStats, charge);
        }

        @Override
        protected void blockChange(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            FakeBlock fakeBlock;
            if (getBukkitEntity().getTicksLived() > 2) {
                fakeBlock = projectileWorld.getFakeBlock(blockRaycastResult.getBlockPos());
                if (fakeBlock != null) {
                    projectileWorld.breakBlock(blockRaycastResult.getBlockPos(), cpShooter);
                }
                fakeBlock = projectileWorld.getFakeBlock(blockRaycastResult.getBlockPos().add(new BlockPosition(0, -2, 0)));
                if (fakeBlock != null) {
                    projectileWorld.breakBlock(blockRaycastResult.getBlockPos().add(new BlockPosition(0, -2, 0)), cpShooter);
                }
                fakeBlock = projectileWorld.getFakeBlock(blockRaycastResult.getBlockPos().add(new BlockPosition(0, -1, 0)));
                if (fakeBlock != null) {
                    projectileWorld.breakBlock(blockRaycastResult.getBlockPos().add(new BlockPosition(0, -1, 0)), cpShooter);
                }
                fakeBlock = projectileWorld.getFakeBlock(blockRaycastResult.getBlockPos().add(new BlockPosition(0, 1, 0)));
                if (fakeBlock != null) {
                    projectileWorld.breakBlock(blockRaycastResult.getBlockPos().add(new BlockPosition(0, 1, 0)), cpShooter);
                }
            }
        }

        @Override
        protected boolean blockBounce(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            return false;
        }

        @Override
        protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            return false;
        }

        @Override
        public void tick() {
            CraftEntity craftEntity = getBukkitEntity();
            if (craftEntity.getTicksLived() > 5 && craftEntity.getLocation().distance(cpShooter.getLocation()) < 2) {
                killEntity();
            } else {
                super.tick();
                if (craftEntity.getTicksLived() >= 0) {
                    craftEntity.setVelocity(craftEntity.getVelocity().add(cpShooter.getPlayer().getEyeLocation().toVector().subtract(new Vector(0, 0.5, 0))
                            .subtract(craftEntity.getLocation().toVector()).normalize().multiply(0.045)));
                }
                GameUtils.spawnParticles(projectileWorld, craftEntity.getLocation().toVector(), Type.OFFENSIVE.getDustSmall(), 2, 0.1);
            }
        }
    }

    private static final ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = BoomerangProjectile.class;
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = false;
        projectileStats.lifeTicks = 80;
        projectileStats.fireRange = 4D;
        projectileStats.collidable = false;
        projectileStats.noClip = true;
        projectileStats.bounces = 1;
        projectileStats.customModelDatas = Lists.newArrayList(12);
    }

    @Override
    public boolean onUse() {
        getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), projectileStats);
        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1.4f);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
