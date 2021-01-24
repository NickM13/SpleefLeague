package com.spleefleague.spleef.game.battle.power.ability.abilities;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.player.SpleefPlayer;

/**
 * @author NickM13
 * @since 5/17/2020
 */
public abstract class AbilityUtility extends Ability {

    protected static AbilityStats init(Class<? extends AbilityUtility> clazz) {
        return AbilityStats.create()
                .setAbilityType(Type.UTILITY)
                .setAbilityClass(clazz);
    }

}
