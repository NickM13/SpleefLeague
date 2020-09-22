package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

/**
 * @author NickM13
 * @since 9/21/2020
 */
public class MobilityPortalGun extends AbilityMobility {

    public static AbilityStats init() {
        return init(MobilityPortalGun.class)
                .setCustomModelData(1)
                .setName("Portal Gun")
                .setDescription("Activate to shoot a portal at the wall. Placing two portals allows players and projectiles to pass through the portal up to %MAX_PASSES% times. A maximum of 2 portals may be active at once.")
                .setUsage(3);
    }

    public static class PortalProjectile extends FakeEntitySnowball {

        public PortalProjectile(GameWorld gameWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
            super(gameWorld, shooter, location, projectileStats, charge);
        }

        @Override
        protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            gameWorld.createPortal(cpShooter, blockRaycastResult.getBlockPos(), blockRaycastResult.getFace());
            killEntity();
            return true;
        }

    }

    private static final int MAX_PASSES = 4;

    private static final ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = PortalProjectile.class;
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = true;
        projectileStats.lifeTicks = 3;
        projectileStats.fireRange = 16D;
        projectileStats.customModelData = 12;
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), projectileStats);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}