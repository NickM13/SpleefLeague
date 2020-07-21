package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Sound;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveStarCannon extends AbilityOffensive {

    private static ProjectileStats boltStats = new ProjectileStats();

    static {
        boltStats.customModelData = 1;
        boltStats.fireRange = 5D;
        boltStats.count = 15;
        boltStats.spread = 30;
        boltStats.collidable = true;
        boltStats.hitKnockback = 1D;
    }

    public OffensiveStarCannon() {
        super(2, 10D);
    }

    @Override
    public String getDisplayName() {
        return "Star Cannon";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Fire a blast of " +
                Chat.STAT + boltStats.count +
                Chat.DESCRIPTION + " snowballs in front of you, destroying blocks hit and slightly knocking back players hit by the blast.";
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        psp.getBattle().getGameWorld().shootProjectile(psp.getCorePlayer(), boltStats);
        psp.getBattle().getGameWorld().playSound(psp.getPlayer().getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 2);
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
