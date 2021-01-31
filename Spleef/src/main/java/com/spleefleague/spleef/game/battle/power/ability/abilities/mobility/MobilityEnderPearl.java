package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.google.common.collect.Lists;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;

/**
 * @author NickM13
 * @since 9/21/2020
 */
public class MobilityEnderPearl extends AbilityMobility {

    public static AbilityStats init() {
        return init(MobilityEnderPearl.class)
                .setCustomModelData(3)
                .setName("Ender Pearl")
                .setDescription("Throw an ender pearl, teleporting to the location it lands.")
                .setUsage(10D);
    }

    private static final ProjectileStats pearlStats = new ProjectileStats();

    public static class EnderPearlProjectile extends FakeEntitySnowball {

        public EnderPearlProjectile(ProjectileWorld projectileWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
            super(projectileWorld, shooter, location, projectileStats, charge);
        }

        @Override
        public void tick() {
            super.tick();
        }

        @Override
        protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            cpShooter.teleport(blockRaycastResult.getRelative().toLocation(cpShooter.getPlayer().getWorld()).add(0.5, 0.5, 0.5));
            projectileWorld.playSound(blockRaycastResult.getIntersection().toLocation(projectileWorld.getWorld()), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1.5f);
            projectileWorld.spawnParticles(Particle.REDSTONE,
                    blockRaycastResult.getIntersection().getX(),
                    blockRaycastResult.getIntersection().getY(),
                    blockRaycastResult.getIntersection().getZ(),
                    40, 1, 1, 1, 0D, Type.MOBILITY.getDustMedium());
            killEntity();
            return true;
        }

    }

    static {
        pearlStats.entityClass = EnderPearlProjectile.class;
        pearlStats.customModelDatas = Lists.newArrayList(13);
        pearlStats.fireRange = 6D;
    }

    private EnderPearlProjectile projectile = null;

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        projectile = (EnderPearlProjectile) getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), pearlStats).get(0);
        return false;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        if (projectile != null && projectile.isAlive()) {
            projectile.killEntity();
        }
    }

}
