package com.spleefleague.core.player.collectible.gear.hookshot;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.EntityRaycastResult;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class HookshotProjectile extends FakeEntitySnowball {

    private static final double HOOKLIFE = 10D;

    private BlockRaycastResult hookedBlock = null;
    private EntityRaycastResult hookedEntity = null;
    private int hookLife = 0;

    public HookshotProjectile(ProjectileWorld<?> projectileWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
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
            if (hookedBlock != null && projectileWorld.isReallySolid(hookedBlock.getBlockPos())) {
                killEntity();
                return;
            }
            if (hookedEntity != null) {
                craftEntity.teleport(hookedEntity.getEntity().getLocation().clone().add(hookedEntity.getOffset()));
            }
            Vector vec = cpShooter.getPlayer().getEyeLocation().toVector();
            Vector dif = craftEntity.getLocation().toVector().subtract(cpShooter.getPlayer().getEyeLocation().toVector());
            for (double i = 1; i < dif.length(); i += 0.5) {
                //GameUtils.spawnParticles(projectileWorld, dif.clone().normalize().multiply(i + Math.random() / 4).add(vec), Type.MOBILITY.getDustSmall(), 1, 0);
            }
        }
    }

    @Override
    public void killEntity() {
        super.killEntity();
        GearHookshot.getHookshotPlayer(cpShooter).setProjectile(null);
    }

}
