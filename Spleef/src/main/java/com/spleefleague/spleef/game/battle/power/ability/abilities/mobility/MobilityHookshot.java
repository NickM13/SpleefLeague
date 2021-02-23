package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.EntityRaycastResult;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.AbilityUtils;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class MobilityHookshot extends AbilityMobility {

    private static final double HOOKLIFE = 2D;

    public static AbilityStats init() {
        return init(MobilityHookshot.class)
                .setCustomModelData(6)
                .setName("Hookshot")
                .setDescription("Fire a grappling hook into a target block, attaching you to it for %HOOKLIFE% seconds. Reactivate while attached to a block to fire yourself in the direction you are facing, destroying blocks you pass.")
                .setUsage(10D);
    }

    public static class HookshotProjectile extends FakeEntitySnowball {

        private BlockRaycastResult hookedBlock = null;
        private EntityRaycastResult hookedEntity = null;
        private int hookLife = 0;

        public HookshotProjectile(ProjectileWorld projectileWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
            super(projectileWorld, shooter, location, projectileStats, charge);
        }

        public Entity getHookedEntity() {
            if (hookedEntity != null) {
                return hookedEntity.getEntity();
            }
            return null;
        }

        public boolean isHooked() {
            return hookedBlock != null || hookedEntity != null;
        }

        public Vector getHookPos() {
            if (hookedBlock != null) {
                switch (hookedBlock.getFace()) {
                    case DOWN:
                        return getBukkitEntity().getLocation().toVector().subtract(new Vector(0, 1.8, 0));
                    case NORTH:
                        return getBukkitEntity().getLocation().toVector().add(new Vector(0, 0, -.3));
                    case EAST:
                        return getBukkitEntity().getLocation().toVector().add(new Vector(.3, 0, 0));
                    case SOUTH:
                        return getBukkitEntity().getLocation().toVector().add(new Vector(0, 0, .3));
                    case WEST:
                        return getBukkitEntity().getLocation().toVector().add(new Vector(-.3, 0, 0));
                    case UP:
                    default:
                        return getBukkitEntity().getLocation().toVector();
                }
            } else if (hookedEntity != null) {
                return getBukkitEntity().getLocation().toVector().subtract(new Vector(0, 1.8, 0));
            }
            return null;
        }

        @Override
        protected void onEntityHit(Entity craftEntity, EntityRaycastResult entityRaycastResult) {
            if (!isHooked()) {
                craftEntity.setGravity(false);
                craftEntity.setVelocity(new Vector(0, 0, 0));
                craftEntity.teleport(entityRaycastResult.getIntersection().toLocation(craftEntity.getWorld()));
                hookedEntity = entityRaycastResult;
                hookLife = craftEntity.getTicksLived() + (int) (HOOKLIFE * 20);
            }
        }

        @Override
        protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            if (!isHooked()) {
                craftEntity.setGravity(false);
                craftEntity.setVelocity(new Vector(0, 0, 0));
                craftEntity.teleport(blockRaycastResult.getIntersection().toLocation(craftEntity.getWorld()));
                hookedBlock = blockRaycastResult;
                hookLife = craftEntity.getTicksLived() + (int) (HOOKLIFE * 20);
            }
            return false;
        }

        @Override
        public void tick() {
            CraftEntity craftEntity = getBukkitEntity();
            if (!isHooked()) {
                super.tick();
            } else if (hookLife < craftEntity.getTicksLived()) {
                killEntity();
            }
            if (isAlive()) {
                if (hookedBlock != null && !projectileWorld.isReallySolid(hookedBlock.getBlockPos())) {
                    killEntity();
                    return;
                }
                if (hookedEntity != null) {
                    craftEntity.teleport(hookedEntity.getEntity().getLocation().clone().add(hookedEntity.getOffset()));
                }
                Vector vec = cpShooter.getPlayer().getEyeLocation().toVector();
                Vector dif = craftEntity.getLocation().toVector().subtract(cpShooter.getPlayer().getEyeLocation().toVector());
                for (double i = 1; i < dif.length(); i += 0.5) {
                    GameUtils.spawnParticles(projectileWorld, dif.clone().normalize().multiply(i + Math.random() / 4).add(vec), Type.MOBILITY.getDustSmall(), 1, 0);
                }
            }
        }
    }

    private static final ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = HookshotProjectile.class;
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = false;
        projectileStats.lifeTicks = 4;
        projectileStats.fireRange = 10D;
        projectileStats.collidable = true;
        projectileStats.size = 1.2D;
        projectileStats.noClip = true;
        projectileStats.bounces = 2;
        projectileStats.customModelDatas = Lists.newArrayList(29);
    }

    private HookshotProjectile hookshot = null;

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {
        if (hookshot != null && hookshot.isHooked()) {
            if (hookshot.isAlive()) {
                getPlayer().setGravity(false);
                Vector dir = hookshot.getHookPos().subtract(getPlayer().getLocation().toVector()).normalize();
                if (hookshot.getHookPos().distance(getPlayer().getLocation().toVector()) < 0.2) {
                    getPlayer().setVelocity(new Vector(0, 0, 0));
                } else {
                    if (hookshot.getHookPos().distance(getPlayer().getLocation().toVector()) > 1.25) {
                        getUser().getBattle().getGameWorld().breakBlocks(getPlayer().getBoundingBox().expand(0.15, 0., 0.15, 0.15, 0.15, 0.15));
                    }
                    getPlayer().setVelocity(dir.multiply(Math.min(1.1, hookshot.getHookPos().distance(getPlayer().getLocation().toVector()) / 5.)));
                }
            } else {
                hookshot = null;
                getPlayer().setGravity(true);
                applyCooldown();
            }
        }
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        if (hookshot == null || !hookshot.isAlive()) {
            hookshot = (HookshotProjectile) getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), projectileStats).get(0);
            getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1.4f);
            applyCooldown(projectileStats.lifeTicks / 20.);
            return false;
        } else {
            Location facing = getPlayer().getLocation().clone();
            facing.setPitch(Math.max(-60, Math.min(60, facing.getPitch())));
            AbilityUtils.startFling(getUser(), facing.getDirection(), 0.3);
            Entity hookedEntity = hookshot.getHookedEntity();
            if (hookedEntity != null) {
                if (hookedEntity instanceof Player) {
                    CorePlayer cp = Core.getInstance().getPlayers().get(hookedEntity.getUniqueId());
                    if (FakeUtils.isOnGround(cp)) {
                        hookedEntity.setVelocity(hookedEntity.getVelocity().clone().setY(0).add(facing.getDirection().clone().normalize().multiply(-0.4)));
                    } else {
                        hookedEntity.setVelocity(facing.getDirection().clone().normalize().multiply(-0.4));
                    }
                } else {
                    hookedEntity.setVelocity(facing.getDirection().clone().normalize().multiply(-0.4));
                }
            }
            hookshot.killEntity();
            hookshot = null;
            return true;
        }
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        hookshot = null;
        getPlayer().setGravity(true);
        AbilityUtils.stopFling(getUser());
    }

}
