package com.spleefleague.core.menu.hotbars.main.friends;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Material;

import java.util.UUID;

public class FriendPendingMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                        .setName("Pending Friend Requests")
                        .setDisplayItem(cp -> {
                            if (cp.getFriends().getIncoming().size() > 0) {
                                return InventoryMenuUtils.MenuIcon.STORE.getIconItem();
                            } else {
                                return InventoryMenuUtils.MenuIcon.LOCKED.getIconItem();
                            }
                        })
                        .setDescription("")
                        .createLinkedContainer("Pending Friend Requests");

        menuItem.getLinkedChest()
                .setPageBoundaries(1, 5, 1, 3)
                .setRefreshAction((container, cp) -> {
                    container.clear();
                    for (UUID uuid : cp.getFriends().getIncoming()) {
                        CorePlayer friend = Core.getInstance().getPlayers().getOffline(uuid);

                        if (friend == null) continue;

                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Accept")
                                .setDisplayItem(InventoryMenuUtils.MenuIcon.ENABLED.getIconItem())
                                .setAction(cp2 -> cp2.getFriends().sendFriendRequest(friend))
                                .setCloseOnAction(false));

                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName(friend.getDisplayName())
                                .setDisplayItem(friend.getSkull())
                                .setCloseOnAction(false));

                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Decline")
                                .setDisplayItem(InventoryMenuUtils.MenuIcon.DISABLED.getIconItem())
                                .setAction(cp2 -> cp2.getFriends().sendFriendDecline(friend))
                                .setCloseOnAction(false));
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
