package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveMeltingBurst extends AbilityOffensive {
    /**
     private static final double BURST_DELAY = 3D;
     private static final double BURST_RADIUS = 5D;

     public static class MeltingProjectile extends EntitySnowball {

     private final GameWorld gameWorld;
     private final ProjectileStats projectileStats;
     private Point lastLoc = null;
     private int bounces;
     private final CorePlayer cpShooter;

     public MeltingProjectile(GameWorld gameWorld, CorePlayer shooter, ProjectileStats projectileStats) {
     super(EntityTypes.SNOWBALL, ((CraftWorld) gameWorld.getWorld()).getHandle());
     this.cpShooter = shooter;

     this.gameWorld = gameWorld;
     this.projectileStats = projectileStats;

     FakeProjectileUtils.shoot(shooter, this, projectileStats);
     lastLoc = new Point(getPositionVector());

     setNoGravity(!projectileStats.gravity);
     this.bounces = projectileStats.bounces;
     this.noclip = projectileStats.noClip;
     }

     @Override
     public void tick() {
     super.tick();
     List<RaycastResult> results = FakeProjectileUtils.tick(cpShooter, gameWorld, this, lastLoc, projectileStats, bounces, null);
     if (!results.isEmpty()) {
     BlockPosition pos;
     RaycastResult result = results.get(0);
     if (result instanceof BlockRaycastResult) {
     pos = ((BlockRaycastResult) result).getBlockPos();
     } else {
     pos = new BlockPosition(
     result.getIntersection().getBlockX(),
     result.getIntersection().getBlockY(),
     result.getIntersection().getBlockZ());
     }
     gameWorld.runTask(Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), () -> {
     gameWorld.breakBlocks(pos, BURST_RADIUS, 1);
     }, (int) (BURST_DELAY * 20)));
     }
     lastLoc = new Point(getPositionVector());
     }

     @Override
     protected void a(MovingObjectPosition var0) {
     if (!noclip) {
     super.a(var0);
     }
     }

     }

     private static ProjectileStats projectileStats = new ProjectileStats();

     static {
     projectileStats.entityClass = MeltingProjectile.class;
     projectileStats.breakRadius = 0D;
     projectileStats.gravity = true;
     projectileStats.fireRange = 2.5D;
     projectileStats.collidable = false;
     projectileStats.noClip = true;
     }
     */

    private static final double BURST_DELAY = 3D;
    private static final double BURST_RADIUS = 5D;

    public static class MeltingProjectile extends FakeEntitySnowball {

        public MeltingProjectile(GameWorld gameWorld, CorePlayer shooter, ProjectileStats projectileStats) {
            super(gameWorld, shooter, projectileStats);
        }

        @Override
        protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            super.blockBounce(craftEntity, blockRaycastResult);
            return true;
        }

        @Override
        public void killEntity() {
            Entity craftEntity = getBukkitEntity();
            BlockPosition pos = new BlockPosition(
                    craftEntity.getLocation().getBlockX(),
                    craftEntity.getLocation().getBlockY(),
                    craftEntity.getLocation().getBlockZ());
            gameWorld.breakBlocks(pos, BURST_RADIUS, 1);
            gameWorld.spawnParticles(Particle.REDSTONE,
                    pos.getX() - 1,
                    pos.getY() - 1,
                    pos.getZ() - 1,
                    150, 2, 2, 2, 0D, Type.OFFENSIVE.getDustMedium());
            super.killEntity();
        }
    }

    private static ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = MeltingProjectile.class;
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = true;
        projectileStats.bounciness = 0.D;
        projectileStats.bounces = 1;
        projectileStats.fireRange = 2.5D;
        projectileStats.lifeTicks = (int) (BURST_DELAY * 20);
        projectileStats.collidable = true;
        projectileStats.noClip = true;
        projectileStats.customModelData = 11;
    }

    public OffensiveMeltingBurst() {
        super(6, 15);
    }

    @Override
    public String getDisplayName() {
        return "Melting Burst";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Throw a sticky bomb, latching onto the first terrain hit. After " +
                Chat.STAT + "1.5" +
                Chat.DESCRIPTION + " seconds, detonate in a large radius.";
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        psp.getBattle().getGameWorld().shootProjectile(psp.getCorePlayer(), projectileStats);
        psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0.8f);
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
