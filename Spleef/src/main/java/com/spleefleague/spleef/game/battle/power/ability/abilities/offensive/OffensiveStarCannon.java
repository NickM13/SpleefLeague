package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.google.common.collect.Lists;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Sound;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveStarCannon extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveStarCannon.class)
                .setCustomModelData(9)
                .setName("Star Cannon")
                .setDescription("Fires a blast of %COUNT% snowballs in front of you, destroying blocks hit and slightly knocking back players hit by the blast.")
                .setUsage(12);
    }

    private static final int COUNT = 15;

    private static ProjectileStats boltStats = new ProjectileStats();

    static {
        boltStats.customModelDatas = Lists.newArrayList(1);
        boltStats.fireRange = 5D;
        boltStats.count = COUNT;
        boltStats.hSpread = 30;
        boltStats.vSpread = 30;
        boltStats.collidable = true;
        boltStats.hitKnockback = 1D;
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), boltStats);
        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 2);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }
}
