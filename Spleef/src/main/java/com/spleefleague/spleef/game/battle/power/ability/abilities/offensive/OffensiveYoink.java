package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.EntityRaycastResult;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import net.minecraft.server.v1_16_R1.MovingObjectPosition;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveYoink extends AbilityOffensive {

    private static final double POWER = 0.35D;
    private static final double POWER_CAP = 2.5D;

    public static class YoinkProjectile extends FakeEntitySnowball {

        public YoinkProjectile(GameWorld gameWorld, CorePlayer shooter, ProjectileStats projectileStats) {
            super(gameWorld, shooter, projectileStats);
        }

        @Override
        protected void a(MovingObjectPosition var0) {
            if (!noclip) {
                super.a(var0);
            }
        }

        @Override
        protected void onEntityHit(Entity craftEntity, EntityRaycastResult entityRaycastResult) {
            killEntity();
            Vector dir = cpShooter.getLocation().toVector().subtract(entityRaycastResult.getEntity().getLocation().toVector());
            dir.setY(0);
            double dist = dir.length();
            dir = dir.normalize();
            entityRaycastResult.getEntity().setVelocity(dir.multiply(Math.min(dist * POWER, POWER_CAP)).add(new Vector(0, 0.25, 0)));
        }

    }

    private static ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = YoinkProjectile.class;
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = false;
        projectileStats.lifeTicks = 80;
        projectileStats.fireRange = 5D;
        projectileStats.collidable = true;
        projectileStats.noClip = true;
        projectileStats.size = 0.5D;
    }

    public OffensiveYoink() {
        super(10, 12D);
    }

    @Override
    public String getDisplayName() {
        return "Yoink";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Fire a hook forward, if the projectile collides with another player they are quickly pulled to the casters locations.";
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        psp.getBattle().getGameWorld().shootProjectile(psp.getCorePlayer(), projectileStats);
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