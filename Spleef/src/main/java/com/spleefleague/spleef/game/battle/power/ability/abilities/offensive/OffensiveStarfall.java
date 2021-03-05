package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.google.common.collect.Lists;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveStarfall extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveStarfall.class)
                .setCustomModelData(10)
                .setName("Starfall")
                .setDescription("Activate to channel a volley of %X15% falling stars every second around your opponent for %MAX_TIME% seconds. During the duration of starfall you are afflicted with Slowness III. Reactivate to cancel.")
                .setUsage(15);
    }

    private static final double MAX_TIME = 4;

    private static final ProjectileStats starStats = new ProjectileStats();

    static {
        starStats.customModelDatas = Lists.newArrayList(1);
        starStats.fireRange = 7D;
        starStats.gravity = false;
        starStats.count = 1;
        starStats.hSpread = 30;
        starStats.vSpread = 30;
        starStats.collidable = false;
    }

    private double starfall = -1;
    private Location location = null;

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        starfall = getUser().getBattle().getRoundTime() + MAX_TIME;
        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 2);
        getPlayer().addPotionEffect(PotionEffectType.SLOW.createEffect(80, 1));
        getUser().setChanneling(false);
        location = getUser().getOpponents().get(0).getPlayer().getLocation().clone().add(0, 20, 0).setDirection(new Vector(0, -1, 0));
        return true;
    }

    /**
     * This is called when a player tried to use an ability while it's on cooldown, used for
     * re-activatable abilities.
     */
    @Override
    protected void onUseCooling() {
        if (starfall > getUser().getBattle().getRoundTime()) {
            getPlayer().removePotionEffect(PotionEffectType.SLOW);
            starfall = -1;
            location = null;
            getUser().setChanneling(false);
        }
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {
        if (starfall > getUser().getBattle().getRoundTime()) {
            Battle<?> battle = getUser().getBattle();
            battle.getGameWorld().shootProjectile(getUser().getCorePlayer(), location, starStats);
        } else if (starfall >= 0) {
            getUser().setChanneling(false);
            starfall = -1;
            location = null;
        }
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        starfall = -1;
        location = null;
    }

}
