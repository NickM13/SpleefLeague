package com.spleefleague.spleef.game.battle.power.ability;

import com.google.common.collect.Lists;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.spleef.game.battle.power.team.PowerSpleefTeamBattle;
import com.spleefleague.spleef.game.battle.power.versus.PowerSpleefVersusBattle;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
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

    protected final static int SHOWN_X = 6, SHOWN_Y = 3;

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
        //addAbilityStats(OffensiveGroundPound.init(), false);
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

        applyHotbarItemStats(Ability.Type.MOBILITY, Lists.newArrayList(PowerSpleefVersusBattle.class,
                PowerSpleefTeamBattle.class), "Place Block", false,
                InventoryMenuAPI.createItemHotbar(Ability.Type.MOBILITY.getSlot(), "not enough pizza"));

        applyHotbarItemStats(Ability.Type.OFFENSIVE, Lists.newArrayList(PowerSpleefVersusBattle.class,
                PowerSpleefTeamBattle.class), "Drop Item", false,
                InventoryMenuAPI.createItemHotbar(Ability.Type.OFFENSIVE.getSlot(), "psOffensiveItem"));

        applyHotbarItemStats(Ability.Type.UTILITY, Lists.newArrayList(PowerSpleefVersusBattle.class,
                PowerSpleefTeamBattle.class), "Swap Item", false,
                InventoryMenuAPI.createItemHotbar(Ability.Type.UTILITY.getSlot(), "psUtilityItem"));

        applyHotbarItemStats(Ability.Type.MOBILITY, Lists.newArrayList(PowerTrainingBattle.class), "Place Block", true,
                (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(Ability.Type.MOBILITY.getSlot(), "too much pizza")
                        .setLinkedContainer(createAbilityMenuItem(Ability.Type.MOBILITY, null).getLinkedChest()));

        applyHotbarItemStats(Ability.Type.OFFENSIVE, Lists.newArrayList(PowerTrainingBattle.class), "Drop Item", true,
                (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(Ability.Type.OFFENSIVE.getSlot(), "pstOffensiveItem")
                        .setLinkedContainer(createAbilityMenuItem(Ability.Type.OFFENSIVE, null).getLinkedChest()));

        applyHotbarItemStats(Ability.Type.UTILITY, Lists.newArrayList(PowerTrainingBattle.class), "Swap Item", true,
                (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(Ability.Type.UTILITY.getSlot(), "pstUtilityItem")
                        .setLinkedContainer(createAbilityMenuItem(Ability.Type.UTILITY, null).getLinkedChest()));
    }

    private static void applyHotbarItemStats(Ability.Type type, List<Class<? extends Battle<?>>> battleClasses, String keybind, boolean swappable, InventoryMenuItemHotbar hotbarItem) {
        hotbarItem
                .setName(cp -> type.getColor() + ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getAbility(type).getName() + " (" + keybind + ")")
                .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getAbility(type).getDisplayItem())
                .setDescription(cp -> (swappable ? "&6&lClick to Change!\n\n" : "") + ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getAbility(type).getDescription())
                .setAvailability(cp -> cp.getBattleState() == BattleState.BATTLER &&
                        battleClasses.contains(cp.getBattle().getClass()) &&
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

    public static InventoryMenuItem createAbilityMenuItem(Ability.Type type, InventoryMenuContainerChest returnContainer) {
        InventoryMenuItemDynamic menuItem = InventoryMenuAPI.createItemDynamic();

        menuItem.setName(type.getColor() + type.getDisplayName() + " Power " + type.getBindName())
                .setDescription(cp -> {
                    AbilityStats selected = getAbility(type, cp.getOptions().getString(type.getOptionName()));
                    return "Select a " + type.getDisplayName() + " power from a selection of &c" +
                            Abilities.getAbilities(type).size() +
                            " &7unique abilities. Only one " + type.getDisplayName() + " ability may be equipped at once." +
                            "\n\n&7&lCurrently Equipped: &6" +
                            (selected != null ? selected.getName() : "Random Power");
                })
                .setDisplayItem(cp -> {
                    AbilityStats selected = getAbility(type, cp.getOptions().getString(type.getOptionName()));
                    if (selected != null) {
                        return selected.getDisplayItem();
                    }
                    return InventoryMenuUtils.createCustomItem(type.getMaterial(), 1);
                })
                .createLinkedContainer(type.getDisplayName() + " Power");

        menuItem.getLinkedChest()
                .setItemBuffer(2)
                .addDeadSpace(0, 2)
                .addDeadSpace(2, 2)
                .addDeadSpace(4, 2);

        menuItem.getLinkedChest()
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    for (AbilityStats abilityStats : Abilities.getAbilities(type).values()) {
                        container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                                .setName(cp2 -> {
                                    AbilityStats selected = getAbility(type, cp.getOptions().getString(type.getOptionName()));
                                    return abilityStats.getType().getColor() + abilityStats.getName() + (selected != null ? (selected.getName().equalsIgnoreCase(abilityStats.getName()) ? " &6(Currently Equipped!)" : "") : "");
                                })
                                .setDescription(abilityStats.getDescription())
                                .setDisplayItem(abilityStats.getDisplayItem())
                                .setAction(cp2 -> {
                                    cp2.getOptions().setString(type.getOptionName(), abilityStats.getName());
                                    if (cp2.getBattle() instanceof PowerTrainingBattle) {
                                        ((PowerTrainingBattle) cp2.getBattle()).updatePowers();
                                    }
                                    if (returnContainer != null) {
                                        cp2.getMenu().setInventoryMenuContainer(returnContainer);
                                    }
                                })
                                .setCloseOnAction(false));
                    }
                });
        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItemDynamic()
                        .setName(type.getColor() + "Random Power")
                        .setDisplayItem(InventoryMenuUtils.createCustomItem(type.getMaterial(), 1))
                        .setDescription("Select a random " + type.getDisplayName() + " power for your next match!")
                        .setAction(cp2 -> {
                            cp2.getOptions().setString(type.getOptionName(), "");
                            if (cp2.getBattle() instanceof PowerTrainingBattle) {
                                ((PowerTrainingBattle) cp2.getBattle()).updatePowers();
                            }
                            if (returnContainer != null) {
                                cp2.getMenu().setInventoryMenuContainer(returnContainer);
                            }
                        })
                        .setCloseOnAction(false),
                2, 3);

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> type.getColor() + getAbility(type, cp.getOptions().getString(type.getOptionName())).getName() + " &6(Currently Equipped!)")
                .setDisplayItem(cp -> getAbility(type, cp.getOptions().getString(type.getOptionName())).getDisplayItem())
                .setDescription(cp -> getAbility(type, cp.getOptions().getString(type.getOptionName())).getDescription())
                .setCloseOnAction(false)
                .setVisibility(cp -> getAbility(type, cp.getOptions().getString(type.getOptionName())) != null), SHOWN_X, SHOWN_Y);
        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> type.getColor() + "Random Power")
                .setDisplayItem(InventoryMenuUtils.createCustomItem(type.getMaterial(), 1))
                .setDescription("Select a random " + type.getDisplayName() + " power for your next match!")
                .setCloseOnAction(false)
                .setVisibility(cp -> getAbility(type, cp.getOptions().getString(type.getOptionName())) == null), SHOWN_X, SHOWN_Y);

        return menuItem;
    }

}
