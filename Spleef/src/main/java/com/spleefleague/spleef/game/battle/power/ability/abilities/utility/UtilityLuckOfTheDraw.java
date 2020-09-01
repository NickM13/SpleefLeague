package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.offensive.OffensiveBouncingBomb;
import com.spleefleague.spleef.game.battle.power.ability.abilities.offensive.OffensiveMeltingBurst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilityLuckOfTheDraw extends AbilityUtility {

    private static List<Ability> abilityWhitelist = null;

    public UtilityLuckOfTheDraw() {
        super(1, 15);
    }

    @Override
    public String getDisplayName() {
        return "Luck of the Draw";
    }

    @Override
    public String getDescription() {
        return "";
    }

    private static final List<Ability> powerWhitelist = new ArrayList<>();

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        if (abilityWhitelist == null) {
            abilityWhitelist = new ArrayList<>();
            abilityWhitelist.add(new OffensiveBouncingBomb());
            abilityWhitelist.add(new OffensiveMeltingBurst());
            abilityWhitelist.add(new UtilityArena());
            abilityWhitelist.add(new UtilityIcePillars());
        }

        Ability selected = (Ability) psp.getPowerValueMap().get("lotd");
        if (selected != null) {
            selected.onUse(psp);
            return true;
        } else {
            Ability randomAbility = abilityWhitelist.get(new Random().nextInt(abilityWhitelist.size()));
            psp.getPowerValueMap().put("lotd", randomAbility);
            psp.getCorePlayer().sendMessage("[LOTD] Random power: " + randomAbility.getDisplayName());
        }
        return false;
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
