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
public abstract class AbilityOffensive extends Ability {

    protected static AbilityStats init(Class<? extends AbilityOffensive> clazz) {
        return AbilityStats.create()
                .setAbilityType(Type.OFFENSIVE)
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
                .setName("&c&lOffensive Power (Drop Item)")
                .setDescription(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
                    return "Select an offensive power from a selection of &c" +
                            Abilities.getAbilities(Type.OFFENSIVE).size() +
                            " &7unique abilities. Only one offensive ability may be equipped at once." +
                            "\n\n&7&lCurrently Equipped: &6" +
                            (sp.getActiveOffensive() != null ? sp.getActiveOffensive().getName() : "Random Power");
                })
                .setDisplayItem(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
                    if (sp.getActiveOffensive() != null) {
                        return sp.getActiveOffensive().getDisplayItem();
                    }
                    return InventoryMenuUtils.createCustomItem(Type.OFFENSIVE.getMaterial(), 11);
                })
                .createLinkedContainer("Offensive Power");

        menuItem.getLinkedChest()
                .setPageBoundaries(1, 3, 1, 7)
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    int i = 0;
                    container.addMenuItem(InventoryMenuAPI.createItem()
                                    .setName(Type.OFFENSIVE.getColor() + "Random Power")
                                    .setDisplayItem(InventoryMenuUtils.createCustomItem(Type.OFFENSIVE.getMaterial(), 11))
                                    .setDescription("Select a random offensive power for your next match!")
                                    .setAction(cp2 -> Spleef.getInstance().getPlayers().get(cp2).setActiveOffensive(""))
                                    .setCloseOnAction(false),
                            0);
                    for (AbilityStats abilityStats : Abilities.getAbilities(Type.OFFENSIVE).values()) {
                        container.addMenuItem(InventoryMenuAPI.createItem()
                                .setName(cp2 -> {
                                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp2);
                                    return abilityStats.getType().getColor() + abilityStats.getName() + (sp.getActiveOffensive() != null ? (sp.getActiveOffensive().getName().equalsIgnoreCase(abilityStats.getName()) ? " &6(Currently Equipped!)" : "") : "");
                                })
                                .setDescription(abilityStats.getDescription())
                                .setDisplayItem(abilityStats.getDisplayItem())
                                .setAction(cp2 -> Spleef.getInstance().getPlayers().get(cp2).setActiveOffensive(abilityStats.getName()))
                                .setCloseOnAction(false), i * 2 + 2);
                        i++;
                    }
                });

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItem()
                .setName(cp -> Type.OFFENSIVE.getColor() + Spleef.getInstance().getPlayers().get(cp).getActiveOffensive().getName() + " &6(Currently Equipped!)")
                .setDisplayItem(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveOffensive().getDisplayItem())
                .setDescription(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveOffensive().getDescription())
                .setCloseOnAction(false)
                .setVisibility(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveOffensive() != null), 4, 4);
        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItem()
                .setName(cp -> Type.OFFENSIVE.getColor() + "Random Power")
                .setDisplayItem(InventoryMenuUtils.createCustomItem(Type.OFFENSIVE.getMaterial(), 11))
                .setDescription("Select a random offensive power for your next match!")
                .setCloseOnAction(false)
                .setVisibility(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveOffensive() == null), 4, 4);

        return menuItem;
    }

}
