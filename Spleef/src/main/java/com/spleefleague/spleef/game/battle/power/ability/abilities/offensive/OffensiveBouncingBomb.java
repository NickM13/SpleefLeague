package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

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
public class OffensiveBouncingBomb extends AbilityOffensive {

    private static final ProjectileStats bombStats = new ProjectileStats();

    public static class BouncingProjectile extends FakeEntitySnowball {

        public BouncingProjectile(GameWorld gameWorld, CorePlayer shooter, ProjectileStats projectileStats) {
            super(gameWorld, shooter, projectileStats);
        }

        @Override
        public void tick() {
            super.tick();
        }

        @Override
        protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            if (super.onBlockHit(craftEntity, blockRaycastResult)) {
                gameWorld.spawnParticles(Particle.REDSTONE,
                        blockRaycastResult.getIntersection().getX(),
                        blockRaycastResult.getIntersection().getY(),
                        blockRaycastResult.getIntersection().getZ(),
                        40, 1, 1, 1, 0D, Type.OFFENSIVE.getDustMedium());
                gameWorld.playSound(blockRaycastResult.getIntersection().toLocation(gameWorld.getWorld()), Sound.ENTITY_LLAMA_SPIT, 1, 1.5f);
                return true;
            }
            return false;
        }

    }

    static {
        bombStats.entityClass = BouncingProjectile.class;
        bombStats.customModelData = 13;
        bombStats.fireRange = 4D;
        bombStats.breakRadius = 2D;
        bombStats.noClip = true;
        bombStats.bounces = 2;
        bombStats.bounciness = 0.65D;
    }

    public OffensiveBouncingBomb() {
        super(1, 10);
    }

    @Override
    public String getDisplayName() {
        return "Bouncing Bomb";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Throw a bouncing bomb forward, destroying blocks in a small radius around its impact point. The bomb may bounce off of surrounding blocks and players up to " +
                Chat.STAT + bombStats.bounces +
                Chat.DESCRIPTION + " times.";
    }

    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        psp.getBattle().getGameWorld().shootProjectile(psp.getCorePlayer(), bombStats);
        psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.BLOCK_COMPOSTER_EMPTY, 1, 1);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {

    }

}
