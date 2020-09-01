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
public abstract class AbilityMobility extends Ability {

    private static InventoryMenuItem menuItem = null;

    public static InventoryMenuItem createMenu() {
        if (menuItem == null) {
            menuItem = createNewMenu();
        }
        return menuItem;
    }

    public static InventoryMenuItem createNewMenu() {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("&a&lMobility Power (Place Block)")
                .setDescription(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
                    return "Select a mobility power from a selection of &c" +
                            Abilities.getAbilities(Type.MOBILITY).size() +
                            " &7unique abilities. Only one mobility ability may be equipped at once." +
                            "\n\n&7&lCurrently Equipped: &6" +
                            (sp.getActiveMobility() != null ? sp.getActiveMobility().getDisplayName() : "Random Power");
                })
                .setDisplayItem(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
                    if (sp.getActiveMobility() != null) {
                        return sp.getActiveMobility().getDisplayItem();
                    }
                    return InventoryMenuUtils.createCustomItem(Type.MOBILITY.getMaterial(), 11);
                })
                .createLinkedContainer("Mobility Power");

        menuItem.getLinkedChest()
                .setPageBoundaries(1, 3, 1, 7)
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    int i = 0;
                    container.addMenuItem(InventoryMenuAPI.createItem()
                                    .setName(Type.MOBILITY.getColor() + "Random Power")
                                    .setDisplayItem(InventoryMenuUtils.createCustomItem(Type.MOBILITY.getMaterial(), 11))
                                    .setDescription("Select a random mobility power for your next match!")
                                    .setAction(cp2 -> Spleef.getInstance().getPlayers().get(cp2).setActiveMobility(""))
                                    .setCloseOnAction(false),
                            0);
                    for (Ability ability : Abilities.getAbilities(Type.MOBILITY).values()) {
                        container.addMenuItem(InventoryMenuAPI.createItem()
                                .setName(cp2 -> {
                                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp2);
                                    return ability.getType().getColor() + ability.getDisplayName() + (sp.getActiveMobility() != null ? (sp.getActiveMobility().getName().equalsIgnoreCase(ability.getName()) ? " &6(Currently Equipped!)" : "") : "");
                                })
                                .setDescription(ability.getFullDescription())
                                .setDisplayItem(ability.getDisplayItem())
                                .setAction(cp2 -> Spleef.getInstance().getPlayers().get(cp2).setActiveMobility(ability.getName()))
                                .setCloseOnAction(false), i * 2 + 2);
                        i++;
                    }
                });

        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItem()
                .setName(cp -> Type.MOBILITY.getColor() + Spleef.getInstance().getPlayers().get(cp).getActiveMobility().getDisplayName() + " &6(Currently Equipped!)")
                .setDisplayItem(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveMobility().getDisplayItem())
                .setDescription(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveMobility().getFullDescription())
                .setCloseOnAction(false)
                .setVisibility(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveMobility() != null), 4, 4);
        menuItem.getLinkedChest().addStaticItem(InventoryMenuAPI.createItem()
                .setName(cp -> Type.MOBILITY.getColor() + "Random Power")
                .setDisplayItem(InventoryMenuUtils.createCustomItem(Type.MOBILITY.getMaterial(), 11))
                .setDescription("Select a random mobility power for your next match!")
                .setCloseOnAction(false)
                .setVisibility(cp -> Spleef.getInstance().getPlayers().get(cp).getActiveMobility() == null), 4, 4);

        return menuItem;
    }

    public AbilityMobility(int customModelData, int charges, double cooldown, double refreshCooldown) {
        super(Type.MOBILITY, InventoryMenuUtils.createCustomItem(Type.MOBILITY.getMaterial(), customModelData), charges, cooldown, refreshCooldown);
    }

    public AbilityMobility(int customModelData, double cooldown) {
        super(Type.MOBILITY, InventoryMenuUtils.createCustomItem(Type.MOBILITY.getMaterial(), customModelData), 1, cooldown, 0.25D);
    }

}
