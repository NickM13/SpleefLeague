package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class MobilityPortalHop extends AbilityMobility {

    public MobilityPortalHop(int customModelData, int charges, double cooldown, double refreshCooldown) {
        super(1, 1, 15, 0.25D);
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        return false;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {

    }
}
