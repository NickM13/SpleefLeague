package com.spleefleague.core.menu.hotbars.main.profile;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuSkullManager;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.party.CoreParty;
import org.bukkit.Material;

import java.util.UUID;

public class PartyMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("Party")
                .setDisplayItem(Material.CAKE, 1)
                .setDescription("View your current party")
                .createLinkedContainer("Party");

        menuItem.getLinkedChest()
                .setOpenAction((container, cp) -> {
                    container.clearSorted();
                    CoreParty party = cp.getParty();
                    if (party != null) {
                        boolean isOwner = party.isOwner(cp);
                        int i = 0;
                        for (UUID uuid : party.getPlayerList()) {
                            CorePlayer cp2 = Core.getInstance().getPlayers().get(uuid);
                            container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                            .setName(cp2.getMenuName())
                                            .setDisplayItem(InventoryMenuSkullManager.getPlayerSkullForced(uuid))
                                            .setCloseOnAction(false),
                                    0, i);
                            container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                            .setName("Poke " + cp2.getMenuName())
                                            .setDisplayItem(Material.BIRCH_SIGN, 1)
                                            .setAction(cp3 -> Chat.sendTell(cp3, cp2, "Poke")),
                                    1, i);
                            container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                            .setName("Kick " + cp2.getMenuName())
                                            .setDisplayItem(InventoryMenuUtils.MenuIcon.LOCKED.getIconItem())
                                            .setCloseOnAction(false),
                                    2, i);
                            container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                            .setName("Transfer to " + cp2.getMenuName())
                                            .setDisplayItem(InventoryMenuUtils.MenuIcon.LOCKED.getIconItem())
                                            .setCloseOnAction(false),
                                    3, i);
                            i++;
                        }
                    }
                });
    }

    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) init();
        return menuItem;
    }

}
