package com.spleefleague.spleef.game.battle.power.ability.abilities;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.player.SpleefPlayer;

/**
 * @author NickM13
 * @since 5/17/2020
 */
public abstract class AbilityOffensive extends Ability {

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
                            (sp.getActiveOffensive() != null ? sp.getActiveOffensive().getDisplayName() : "Random Power");
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
                    for (Ability ability : Abilities.getAbilities(Type.OFFENSIVE).values()) {
                        container.addMenuItem(InventoryMenuAPI.createItem()
                                .setName(cp2 -> {
                                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp2);
                                    return ability.getType().getColor() + ability.getDisplayName() + (sp.getActiveOffensive() != null ? (sp.getActiveOffensive().getName().equalsIgnoreCase(ability.getName()) ? " &6(Currently Equipped!)" : "") : "");
                                })
                                .setDescription(ability.getFullDescription())
                                .setDisplayItem(ability.getDisplayItem())
                                .setAction(cp2 -> Spleef.getInstance().getPlayers().get(cp2).setActiveOffensive(ability.getName()))
                                .setCloseOnAction(false), i * 2 + 2);
                        i++;
                    }
                });

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItem()
                .setName(cp -> Type.OFFENSIVE.getColor() + Spleef.getInstance().getPlayers().get(cp).getActiveOffensive().getDisplayName() + " &6(Currently Equipped!)")
                .setDisplayItem(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveOffensive().getDisplayItem())
                .setDescription(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveOffensive().getFullDescription())
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

    public AbilityOffensive(int customModelData, int charges, double cooldown, double refreshCooldown) {
        super(Type.OFFENSIVE, InventoryMenuUtils.createCustomItem(Type.OFFENSIVE.getMaterial(), customModelData), charges, cooldown, refreshCooldown);
    }

    public AbilityOffensive(int customModelData, double cooldown) {
        super(Type.OFFENSIVE, InventoryMenuUtils.createCustomItem(Type.OFFENSIVE.getMaterial(), customModelData), 1, cooldown, 0.25D);
    }

}
