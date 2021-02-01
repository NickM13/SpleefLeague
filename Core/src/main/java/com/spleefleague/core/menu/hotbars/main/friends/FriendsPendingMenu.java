package com.spleefleague.core.menu.hotbars.main.friends;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuSkullManager;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author NickM13
 */
public class FriendsPendingMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = InventoryMenuAPI.createItemStatic()
                .setName("Pending Friend Requests")
                .setDisplayItem(Material.FEATHER, 1)
                .setDescription("Uhh")
                .createLinkedContainer("Pending Friend Requests");

        menuItem.getLinkedChest()
                .setOpenAction((container, cp) -> {
                    container.clear();
                    for (UUID uuid : cp.getFriends().getIncoming()) {
                        CorePlayer friend = Core.getInstance().getPlayers().getOffline(uuid);

                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Accept")
                                .setDisplayItem(InventoryMenuUtils.MenuIcon.ENABLED.getIconItem())
                                .setAction(cp2 -> cp2.getFriends().sendFriendRequest(friend)));
                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setDisplayItem(InventoryMenuSkullManager.getPlayerSkull(uuid))
                                .setName(friend.getName()));
                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Decline")
                                .setDisplayItem(InventoryMenuUtils.MenuIcon.DISABLED.getIconItem())
                                .setAction(cp2 -> cp2.getFriends().sendFriendDecline(friend)));
                    }
                })
                .setPageBoundaries(1, 5, 1, 3);
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
