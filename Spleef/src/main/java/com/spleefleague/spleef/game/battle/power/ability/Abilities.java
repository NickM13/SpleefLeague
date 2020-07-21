package com.spleefleague.spleef.game.battle.power.ability;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.spleef.game.battle.power.PowerSpleefBattle;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.utility.*;
import com.spleefleague.spleef.game.battle.power.ability.abilities.mobility.*;
import com.spleefleague.spleef.game.battle.power.ability.abilities.offensive.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author NickM13
 * @since 5/17/2020
 */
public class Abilities {

    private static Map<Ability.Type, SortedMap<String, Ability>> abilityMap = new HashMap<>();

    public static void init() {
        for (Ability.Type type : Ability.Type.values()) {
            abilityMap.put(type, new TreeMap<>(String::compareTo));
        }

        addAbility(new OffensiveBoomerang());
        addAbility(new OffensiveBouncingBomb());
        addAbility(new OffensiveStarCannon());
        addAbility(new OffensiveIntoTheShadows());
        addAbility(new OffensiveLivingBomb());
        addAbility(new OffensiveMeltingBurst());
        addAbility(new OffensivePunch());
        addAbility(new OffensiveRiptide());
        addAbility(new OffensiveRollerSpades());
        addAbility(new OffensiveYoink());

        addAbility(new UtilityArena());
        addAbility(new UtilityIcePillar());
        //addAbility(new UtilityIcePillars());
        addAbility(new UtilityRegeneration());
        addAbility(new UtilitySafetyZone());
        addAbility(new UtilitySmokeBomb());
        addAbility(new UtilityWall());
        //addAbility(new UtilityLuckOfTheDraw());

        addAbility(new MobilityAirDash());
        addAbility(new MobilityEnderRift());
        addAbility(new MobilityHeroicLeap());
        addAbility(new MobilityHookshot());
        addAbility(new MobilityJetpack());

        InventoryMenuAPI.createItemHotbar(Ability.Type.MOBILITY.getSlot(), "psMobilityItem")
                .setName(cp -> "&a&l" + ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility().getDisplayName() + " (Place Block)")
                .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility().getDisplayItem())
                .setDescription(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility().getFullDescription())
                .setAvailability(cp -> cp.getBattleState() == BattleState.BATTLER &&
                        cp.getBattle() instanceof PowerSpleefBattle &&
                        cp.getBattle().getBattler(cp) != null &&
                        ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility() != null)
                .setVisibility(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility() != null);

        InventoryMenuAPI.createItemHotbar(Ability.Type.OFFENSIVE.getSlot(), "psOffensiveItem")
                .setName(cp -> "&c&l" + ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getOffensive().getDisplayName() + " (Drop Item)")
                .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getOffensive().getDisplayItem())
                .setDescription(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getOffensive().getFullDescription())
                .setAvailability(cp -> cp.getBattleState() == BattleState.BATTLER &&
                        cp.getBattle() instanceof PowerSpleefBattle &&
                        cp.getBattle().getBattler(cp) != null &&
                        ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getOffensive() != null)
                .setVisibility(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getOffensive() != null);

        InventoryMenuAPI.createItemHotbar(Ability.Type.UTILITY.getSlot(), "psUtilityItem")
                .setName(cp -> "&9&l" + ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getUtility().getDisplayName() + " (Swap Item)")
                .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getUtility().getDisplayItem())
                .setDescription(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getUtility().getFullDescription())
                .setAvailability(cp -> cp.getBattleState() == BattleState.BATTLER &&
                        cp.getBattle() instanceof PowerSpleefBattle &&
                        cp.getBattle().getBattler(cp) != null &&
                        ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getUtility() != null)
                .setVisibility(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getUtility() != null);
    }

    private static void addAbility(Ability ability) {
        abilityMap.get(ability.getType()).put(ability.getName(), ability);
    }

    public static Ability getAbilityRandom(Ability.Type type) {
        int to = new Random().nextInt(abilityMap.get(type).size());
        Iterator<Ability> it = abilityMap.get(type).values().iterator();
        int i = 0;
        while (it.hasNext()) {
            Ability ability = it.next();
            if (i == to) {
                return ability;
            }
            i++;
        }
        return null;
    }

    public static Map<String, Ability> getAbilities(Ability.Type type) {
        return abilityMap.get(type);
    }

    public static Ability getAbility(Ability.Type type, String name) {
        if (name == null) return null;
        return abilityMap.get(type).get(name);
    }

}
