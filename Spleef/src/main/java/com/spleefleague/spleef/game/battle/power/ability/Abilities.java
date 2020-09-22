package com.spleefleague.spleef.game.battle.power.ability;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.spleef.game.battle.power.PowerSpleefBattle;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.offensive.*;
import com.spleefleague.spleef.game.battle.power.ability.abilities.utility.*;
import com.spleefleague.spleef.game.battle.power.ability.abilities.mobility.*;
import com.spleefleague.spleef.game.battle.power.training.PowerTrainingBattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author NickM13
 * @since 5/17/2020
 */
public class Abilities {

    private static final Map<Ability.Type, SortedMap<String, AbilityStats>> abilityMap = new HashMap<>();
    private static final List<AbilityStats> randomAbilities = new ArrayList<>();

    public static void init() {
        for (Ability.Type type : Ability.Type.values()) {
            abilityMap.put(type, new TreeMap<>(String::compareTo));
        }

        addAbilityStats(OffensiveBoomerang.init(), false);
        addAbilityStats(OffensiveBouncingBomb.init(), false);
        addAbilityStats(OffensiveStarCannon.init(), false);
        addAbilityStats(OffensiveIntoTheShadows.init(), false);
        addAbilityStats(OffensiveLivingBomb.init(), false);
        addAbilityStats(OffensiveMeltingBurst.init(), false);
        addAbilityStats(OffensivePunch.init(), false);
        addAbilityStats(OffensiveGroundPound.init(), false);
        addAbilityStats(MobilityRollerSpades.init(), false);
        addAbilityStats(OffensiveStarfall.init(), false);
        addAbilityStats(OffensiveYoink.init(), false);

        addAbilityStats(UtilityArena.init(), true);
        addAbilityStats(UtilityIcePillar.init(), true);
        addAbilityStats(UtilityLuckyDraw.init(), false);
        addAbilityStats(UtilityRegeneration.init(), true);
        addAbilityStats(UtilitySafetyZone.init(), true);
        addAbilityStats(UtilitySmokeBomb.init(), true);
        addAbilityStats(UtilityWall.init(), true);

        addAbilityStats(MobilityAirDash.init(), false);
        addAbilityStats(MobilityEnderRift.init(), false);
        addAbilityStats(MobilityHeroicLeap.init(), false);
        addAbilityStats(MobilityHookshot.init(), false);
        addAbilityStats(MobilityJetpack.init(), false);
        addAbilityStats(MobilityPortalGun.init(), false);

        applyHotbarItemStats(Ability.Type.MOBILITY, PowerSpleefBattle.class, "Place Block", false,
                InventoryMenuAPI.createItemHotbar(Ability.Type.MOBILITY.getSlot(), "not enough pizza"));

        applyHotbarItemStats(Ability.Type.OFFENSIVE, PowerSpleefBattle.class, "Drop Item", false,
                InventoryMenuAPI.createItemHotbar(Ability.Type.OFFENSIVE.getSlot(), "psOffensiveItem"));

        applyHotbarItemStats(Ability.Type.UTILITY, PowerSpleefBattle.class, "Swap Item", false,
                InventoryMenuAPI.createItemHotbar(Ability.Type.UTILITY.getSlot(), "psUtilityItem"));

        applyHotbarItemStats(Ability.Type.MOBILITY, PowerTrainingBattle.class, "Place Block", true,
                (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(Ability.Type.MOBILITY.getSlot(), "too much pizza")
                        .setLinkedContainer(AbilityMobility.createNewMenu().getLinkedChest()));

        applyHotbarItemStats(Ability.Type.OFFENSIVE, PowerTrainingBattle.class, "Drop Item", true,
                (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(Ability.Type.OFFENSIVE.getSlot(), "pstOffensiveItem")
                        .setLinkedContainer(AbilityOffensive.createNewMenu().getLinkedChest()));

        applyHotbarItemStats(Ability.Type.UTILITY, PowerTrainingBattle.class, "Swap Item", true,
                (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(Ability.Type.UTILITY.getSlot(), "pstUtilityItem")
                .setLinkedContainer(AbilityUtility.createNewMenu().getLinkedChest()));
    }

    private static void applyHotbarItemStats(Ability.Type type, Class<? extends Battle<?>> battleClass, String keybind, boolean swappable, InventoryMenuItemHotbar hotbarItem) {
        hotbarItem
                .setName(cp -> type.getColor() + ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getAbility(type).getName() + " (" + keybind + ")")
                .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getAbility(type).getDisplayItem())
                .setDescription(cp -> (swappable ? "&6&lClick to Change!\n\n" : "") + ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getAbility(type).getDescription())
                .setAvailability(cp -> cp.getBattleState() == BattleState.BATTLER &&
                        cp.getBattle().getClass().equals(battleClass) &&
                        ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getAbility(type) != null)
                .setVisibility(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getAbility(type) != null);
    }

    private static void addAbilityStats(AbilityStats abilityStats, boolean canLuckyDraw) {
        abilityMap.get(abilityStats.getType()).put(abilityStats.getName(), abilityStats.build());
        if (canLuckyDraw) {
            randomAbilities.add(abilityStats);
        }
    }

    public static AbilityStats getRandomAbilityStats(Ability.Type type) {
        int to = new Random().nextInt(abilityMap.get(type).size());
        Iterator<AbilityStats> it = abilityMap.get(type).values().iterator();
        int i = 0;
        while (it.hasNext()) {
            AbilityStats ability = it.next();
            if (i == to) {
                return ability;
            }
            i++;
        }
        return null;
    }

    public static AbilityStats getRandomAbilityStats() {
        return randomAbilities.get(new Random().nextInt(randomAbilities.size()));
    }

    public static Map<String, AbilityStats> getAbilities(Ability.Type type) {
        return abilityMap.get(type);
    }

    public static AbilityStats getAbility(Ability.Type type, String name) {
        if (name == null) return null;
        return abilityMap.get(type).get(name);
    }

}
