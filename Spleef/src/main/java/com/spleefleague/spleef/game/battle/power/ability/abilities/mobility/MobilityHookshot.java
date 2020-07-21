package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.EntityRaycastResult;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.offensive.OffensiveBoomerang;
import org.bukkit.Location;
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

    private static final double HOOK_LIFE = 2D;

    public static class HookshotProjectile extends FakeEntitySnowball {

        private BlockRaycastResult hookedBlock = null;
        private EntityRaycastResult hookedEntity = null;
        private int hookLife = 0;

        public HookshotProjectile(GameWorld gameWorld, CorePlayer shooter, ProjectileStats projectileStats) {
            super(gameWorld, shooter, projectileStats);
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
                hookLife = craftEntity.getTicksLived() + (int) (HOOK_LIFE * 20);
            }
        }

        @Override
        protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            if (!isHooked()) {
                craftEntity.setGravity(false);
                craftEntity.setVelocity(new Vector(0, 0, 0));
                craftEntity.teleport(blockRaycastResult.getIntersection().toLocation(craftEntity.getWorld()));
                hookedBlock = blockRaycastResult;
                hookLife = craftEntity.getTicksLived() + (int) (HOOK_LIFE * 20);
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
                if (hookedEntity != null) {
                    craftEntity.teleport(hookedEntity.getEntity().getLocation().clone().add(hookedEntity.getOffset()));
                }
                Vector vec = cpShooter.getPlayer().getEyeLocation().toVector();
                Vector dif = craftEntity.getLocation().toVector().subtract(cpShooter.getPlayer().getEyeLocation().toVector());
                for (double i = 1; i < dif.length(); i += 0.5) {
                    GameUtils.spawnParticles(gameWorld, dif.clone().normalize().multiply(i + Math.random() / 4).add(vec), Type.MOBILITY.getDustSmall(), 1, 0);
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
        projectileStats.fireRange = 7D;
        projectileStats.collidable = true;
        projectileStats.size = 0.5;
        projectileStats.noClip = true;
        projectileStats.bounces = 1;
        projectileStats.customModelData = 12;
    }

    public MobilityHookshot() {
        super(6, 10);
    }

    @Override
    public String getDisplayName() {
        return "Hookshot";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Fire a grappling hook into a target block, attaching you to it for " +
                Chat.STAT + HOOK_LIFE +
                Chat.DESCRIPTION + " seconds. Reactivate while attached to a block to fire yourself in the direction you are facing, destroying blocks you pass.";
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     *
     * @param psp Casting Player
     */
    @Override
    public void update(PowerSpleefPlayer psp) {
        HookshotProjectile hookshot = (HookshotProjectile) psp.getPowerValueMap().get("hookshot");
        double hookshotTime = (double) psp.getPowerValueMap().get("hookshottime");
        Vector hookshotDir = (Vector) psp.getPowerValueMap().get("hookshotdir");
        if (hookshot != null) {
            if (hookshot.isHooked()) {
                if (hookshot.isAlive()) {
                    psp.getPlayer().setGravity(false);
                    Vector dir = hookshot.getHookPos().subtract(psp.getPlayer().getLocation().toVector()).normalize();
                    if (hookshot.getHookPos().distance(psp.getPlayer().getLocation().toVector()) < 0.2) {
                        psp.getPlayer().setVelocity(new Vector(0, 0, 0));
                    } else {
                        if (hookshot.getHookPos().distance(psp.getPlayer().getLocation().toVector()) > 1.25) {
                            psp.getBattle().getGameWorld().breakBlocks(psp.getPlayer().getBoundingBox().expand(0.15, 0., 0.15, 0.15, 0.15, 0.15));
                        }
                        psp.getPlayer().setVelocity(dir.multiply(Math.min(1.1, hookshot.getHookPos().distance(psp.getPlayer().getLocation().toVector()) / 5.)));
                    }
                } else {
                    psp.getPowerValueMap().put("hookshot", null);
                    psp.getPlayer().setGravity(true);
                    applyCooldown(psp);
                }
            }
        }
        if (hookshotTime >= 0) {
            if (hookshotTime < psp.getBattle().getRoundTime()) {
                psp.getPowerValueMap().put("hookshottime", -1D);
                psp.getPowerValueMap().put("hookshotdir", null);
                psp.getPlayer().setGravity(true);
            } else {
                psp.getPlayer().setVelocity(hookshotDir.clone().multiply(1.2));
                psp.getBattle().getGameWorld().breakBlocks(psp.getPlayer().getBoundingBox().expand(0.15, -0.05, 0.15, 0.15, 0.15, 0.15));
            }
        }
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        HookshotProjectile hookshot = (HookshotProjectile) psp.getPowerValueMap().get("hookshot");
        if (hookshot == null || !hookshot.isAlive()) {
            hookshot = (HookshotProjectile) psp.getBattle().getGameWorld().shootProjectile(psp.getCorePlayer(), projectileStats).get(0);
            psp.getPowerValueMap().put("hookshot", hookshot);
            psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1.4f);
            applyCooldown(psp, projectileStats.lifeTicks / 20.);
            return false;
        } else {
            psp.getPowerValueMap().put("hookshot", null);
            psp.getPlayer().setGravity(true);
            psp.getPowerValueMap().put("hookshottime", psp.getBattle().getRoundTime() + 0.3);
            Location facing = psp.getPlayer().getLocation().clone();
            facing.setPitch(Math.max(-60, Math.min(60, facing.getPitch())));
            psp.getPowerValueMap().put("hookshotdir", facing.getDirection());
            psp.getPlayer().setVelocity(facing.getDirection().clone().normalize().multiply(1.1));
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
            return true;
        }
    }

    /**
     * Called at the start of a round
     *
     * @param psp Casting Player
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {
        psp.getPowerValueMap().put("hookshot", null);
        psp.getPowerValueMap().put("hookshottime", -1D);
        psp.getPowerValueMap().put("hookshotdir", null);
        psp.getPlayer().setGravity(true);
    }

}
