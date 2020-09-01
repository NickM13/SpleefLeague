package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilityChimes extends AbilityUtility {

    public UtilityChimes() {
        super(3, 1, 0, 0);
    }

    @Override
    public String getDisplayName() {
        return "Chimes";
    }

    @Override
    public String getDescription() {
        return "Something could go here I suppose";
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
