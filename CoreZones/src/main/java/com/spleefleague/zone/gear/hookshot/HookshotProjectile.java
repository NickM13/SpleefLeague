package com.spleefleague.zone.gear.hookshot;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.EntityRaycastResult;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import com.spleefleague.core.world.global.GlobalWorld;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class HookshotProjectile extends FakeEntitySnowball {

    private static final double REELTIME = 5D;
    private static final double HOOKLIFE = 3D;

    private BlockRaycastResult hookedBlock = null;
    private EntityRaycastResult hookedEntity = null;
    private int reelTime = 0;
    private int hookLife = 0;
    private boolean toKill = false;

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
        /*
        if (!isHooked()) {
            craftEntity.setGravity(false);
            craftEntity.setVelocity(new Vector(0, 0, 0));
            craftEntity.teleport(entityRaycastResult.getIntersection().toLocation(craftEntity.getWorld()));
            hookedEntity = entityRaycastResult;
            reelTime = craftEntity.getTicksLived() + (int) (REELTIME * 20);
            hookLife = craftEntity.getTicksLived() + (int) (HOOKLIFE * 20);
            cpShooter.getPlayer().setGravity(false);
        }
         */
    }

    private static final Material HOOK_BLOCK = Material.CHISELED_STONE_BRICKS;

    @Override
    protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult, Vector direction) {
        if (!isHooked()) {
            BlockPosition pos = blockRaycastResult.getBlockPos();
            Material material = projectileWorld.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getType();
            if (material.equals(HOOK_BLOCK)) {
                craftEntity.setGravity(false);
                craftEntity.setVelocity(new Vector(0, 0, 0));
                craftEntity.teleport(blockRaycastResult.getIntersection().toLocation(craftEntity.getWorld()));
                hookedBlock = blockRaycastResult;
                reelTime = craftEntity.getTicksLived() + (int) (REELTIME * 20);
                hookLife = craftEntity.getTicksLived() + (int) (HOOKLIFE * 20);
                ((GlobalWorld) projectileWorld).playSound(pos.toLocation(projectileWorld.getWorld()), Sound.BLOCK_ANVIL_LAND, 1, 1, "Sound:Gadget");
                cpShooter.getPlayer().setGravity(false);
            } else {
                killEntity();
            }
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        CraftEntity craftEntity = getBukkitEntity();
        if (!isHooked()) {
            super.tick();
        } else if (hookLife < craftEntity.getTicksLived() || cpShooter.getPlayer().isSneaking() || toKill) {
            killEntity();
            if (toKill) {
                cpShooter.getPlayer().setVelocity(cpShooter.getPlayer().getLocation().getDirection().multiply(1));
            }
            return;
        }

        if (hookedBlock != null) {
            Vector diff = getHookPos().subtract(cpShooter.getPlayer().getLocation().toVector());
            if (getHookPos().distance(cpShooter.getPlayer().getLocation().toVector()) < 0.2) {
                cpShooter.getPlayer().setVelocity(new Vector(0, 0, 0));
                if (reelTime > 0) {
                    reelTime = -1;
                    hookLife = craftEntity.getTicksLived() + (int) (HOOKLIFE * 20);
                }
            } else {
                cpShooter.getPlayer().setVelocity(diff.normalize().multiply(Math.min(1., getHookPos().distance(cpShooter.getPlayer().getLocation().toVector()) / 5.)));
            }
        }
        if (hookedEntity != null) {
            craftEntity.teleport(hookedEntity.getEntity().getLocation().clone().add(hookedEntity.getOffset()));
        }
    }

    public void attemptKill() {
        toKill = true;
    }

    @Override
    public void killEntity() {
        super.killEntity();
        GearHookshot.getHookshotPlayer(cpShooter).setProjectile(null);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> cpShooter.getPlayer().setGravity(true), 2L);
    }

}
