package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/18/2020
 */
public class OffensiveBoomerang extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveBoomerang.class)
                .setCustomModelData(12)
                .setName("Boomerang")
                .setDescription("Throw a boomerang forward destroying all destructible blocks it passes, returning to the sender after %X1% second.")
                .setUsage(3);
    }

    public static class BoomerangProjectile extends FakeEntitySnowball {

        Vector targetDir = null;
        int distanceTravelled;

        public BoomerangProjectile(GameWorld gameWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
            super(gameWorld, shooter, location, projectileStats, charge);
        }

        @Override
        protected void blockChange(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            if (getBukkitEntity().getTicksLived() > 2) {
                if (gameWorld.getFakeBlocks().containsKey(blockRaycastResult.getBlockPos()) &&
                        !gameWorld.getFakeBlocks().get(blockRaycastResult.getBlockPos()).getBlockData().getMaterial().isAir()) {
                    gameWorld.breakBlocks(blockRaycastResult.getBlockPos(), 1, 1);
                }
                if (gameWorld.getFakeBlocks().containsKey(blockRaycastResult.getBlockPos().add(new BlockPosition(0, -2, 0))) &&
                        !gameWorld.getFakeBlocks().get(blockRaycastResult.getBlockPos().add(new BlockPosition(0, -2, 0))).getBlockData().getMaterial().isAir()) {
                    gameWorld.breakBlocks(blockRaycastResult.getBlockPos().add(new BlockPosition(0, -2, 0)), 1, 1);
                }
                if (gameWorld.getFakeBlocks().containsKey(blockRaycastResult.getBlockPos().add(new BlockPosition(0, -1, 0))) &&
                        !gameWorld.getFakeBlocks().get(blockRaycastResult.getBlockPos().add(new BlockPosition(0, -1, 0))).getBlockData().getMaterial().isAir()) {
                    gameWorld.breakBlocks(blockRaycastResult.getBlockPos().add(new BlockPosition(0, -1, 0)), 1, 1);
                }
                if (gameWorld.getFakeBlocks().containsKey(blockRaycastResult.getBlockPos().add(new BlockPosition(0, 1, 0))) &&
                        !gameWorld.getFakeBlocks().get(blockRaycastResult.getBlockPos().add(new BlockPosition(0, 1, 0))).getBlockData().getMaterial().isAir()) {
                    gameWorld.breakBlocks(blockRaycastResult.getBlockPos().add(new BlockPosition(0, 1, 0)), 1, 1);
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
                GameUtils.spawnParticles(gameWorld, craftEntity.getLocation().toVector(), Type.OFFENSIVE.getDustSmall(), 2, 0.1);
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
        projectileStats.customModelData = 12;
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
