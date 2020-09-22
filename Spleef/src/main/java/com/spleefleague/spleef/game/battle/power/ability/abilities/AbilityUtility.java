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

    private static InventoryMenuItem menuItem;

    public static InventoryMenuItem createMenu() {
        if (menuItem == null) {
            menuItem = createNewMenu();
        }
        return menuItem;
    }

    public static InventoryMenuItem createNewMenu() {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("&9&lUtility Power (Swap Item)")
                .setDescription(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
                    return "Select a utility power from a selection of &c" +
                            Abilities.getAbilities(Type.UTILITY).size() +
                            " &7unique abilities. Only one utility ability may be equipped at once." +
                            "\n\n&7&lCurrently Equipped: &6" +
                            (sp.getActiveUtility() != null ? sp.getActiveUtility().getName() : "Random Power");
                })
                .setDisplayItem(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
                    if (sp.getActiveUtility() != null) {
                        return sp.getActiveUtility().getDisplayItem();
                    }
                    return InventoryMenuUtils.createCustomItem(Type.UTILITY.getMaterial(), 11);
                })
                .createLinkedContainer("Utility Power");

        menuItem.getLinkedChest()
                .setPageBoundaries(1, 3, 1, 7)
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    int i = 0;
                    container.addMenuItem(InventoryMenuAPI.createItem()
                                    .setName(Type.UTILITY.getColor() + "Random Power")
                                    .setDisplayItem(InventoryMenuUtils.createCustomItem(Type.UTILITY.getMaterial(), 11))
                                    .setDescription("Select a random utility power for your next match!")
                                    .setAction(cp2 -> Spleef.getInstance().getPlayers().get(cp2).setActiveUtility(""))
                                    .setCloseOnAction(false),
                            0);
                    for (AbilityStats abilityStats : Abilities.getAbilities(Type.UTILITY).values()) {
                        container.addMenuItem(InventoryMenuAPI.createItem()
                                .setName(cp2 -> {
                                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp2);
                                    return abilityStats.getType().getColor() + abilityStats.getName() + (sp.getActiveUtility() != null ? (sp.getActiveUtility().getName().equalsIgnoreCase(abilityStats.getName()) ? " &6(Currently Equipped!)" : "") : "");
                                })
                                .setDescription(abilityStats.getDescription())
                                .setDisplayItem(abilityStats.getDisplayItem())
                                .setAction(cp2 -> Spleef.getInstance().getPlayers().get(cp2).setActiveUtility(abilityStats.getName()))
                                .setCloseOnAction(false), i * 2 + 2);
                        i++;
                    }
                });

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItem()
                .setName(cp -> Type.UTILITY.getColor() + Spleef.getInstance().getPlayers().get(cp).getActiveUtility().getName() + " &6(Currently Equipped!)")
                .setDisplayItem(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveUtility().getDisplayItem())
                .setDescription(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveUtility().getDescription())
                .setCloseOnAction(false)
                .setVisibility(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveUtility() != null), 4, 4);
        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItem()
                .setName(cp -> Type.UTILITY.getColor() + "Random Power")
                .setDisplayItem(InventoryMenuUtils.createCustomItem(Type.UTILITY.getMaterial(), 11))
                .setDescription("Select a random utility power for your next match!")
                .setCloseOnAction(false)
                .setVisibility(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveUtility() == null), 4, 4);

        return menuItem;
    }

}
