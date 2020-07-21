package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import net.minecraft.server.v1_15_R1.MovingObjectPosition;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilitySmokeBomb extends AbilityUtility {

    private static final double DURATION = 5D;
    private static final double RANGE = 4D;

    public static class SmokeBombProjectile extends FakeEntitySnowball {

        private boolean activated = false;

        public SmokeBombProjectile(GameWorld gameWorld, CorePlayer shooter, ProjectileStats projectileStats) {
            super(gameWorld, shooter, projectileStats);
        }

        @Override
        public void tick() {
            super.tick();
            if (activated) {
                Location loc = new Location(gameWorld.getWorld(),
                        getPositionVector().getX(),
                        getPositionVector().getY() + 0.5,
                        getPositionVector().getZ());
                for (BattlePlayer bp : cpShooter.getBattle().getBattlers()) {
                    if (!bp.getCorePlayer().equals(cpShooter) &&
                            bp.getPlayer().getLocation().distance(loc) <= RANGE) {
                        bp.getPlayer().addPotionEffect(PotionEffectType.BLINDNESS.createEffect(30, 0));
                    }
                }
                GameUtils.spawnRingParticles(gameWorld, loc.toVector(), Type.UTILITY.getDustMedium(), RANGE, 10);
                gameWorld.playSound(loc, Sound.ENTITY_CAT_PURR, 0.25f, 2);
            }
        }

        @Override
        protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
            super.blockBounce(craftEntity, blockRaycastResult);
            if (!activated) {
                activated = true;
                this.lifeTicks = craftEntity.getTicksLived() + (int) (DURATION * 20);
            }
            return true;
        }

    }

    private static ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = SmokeBombProjectile.class;
        projectileStats.customModelData = 10;
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = true;
        projectileStats.lifeTicks = 200;
        projectileStats.fireRange = 3.5D;
        projectileStats.collidable = false;
        projectileStats.noClip = true;
        projectileStats.bounces = 1;
        projectileStats.bounciness = 0.15;
    }

    public UtilitySmokeBomb() {
        super(5, 15);
    }

    @Override
    public String getDisplayName() {
        return "Smoke Bomb";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Throw a smoke bomb onto the ground lasting " +
                Chat.STAT + DURATION +
                Chat.DESCRIPTION + " seconds, blinding players in a small radius around the bomb.";
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
