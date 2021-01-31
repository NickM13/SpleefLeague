package com.spleefleague.core.menu.hotbars.main.friends;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import org.bukkit.Material;

public class FriendOptionsMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = InventoryMenuAPI.createItemStatic()
                        .setName("Friend Options")
                        .setDisplayItem(Material.FEATHER, 1)
                        .setDescription("Uhh")
                        .createLinkedContainer("Friend Options");

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Friend Requests")
                                .setDisplayItem(Material.FEATHER, 1)
                                .setDescription("Allow incoming friend requests"),
                        0, 0);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemToggle()
                                .setEnabledFun(cp -> !cp.getFriends().isBlockingRequests())
                                .setAction(cp -> cp.getFriends().setBlockingRequests(!cp.getFriends().isBlockingRequests())),
                        1, 0);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Friend Logins")
                                .setDisplayItem(Material.FEATHER, 1)
                                .setDescription("Receive friend login notifications"),
                        0, 1);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemToggle()
                                .setEnabledFun(cp -> !cp.getFriends().isBlockingLogins())
                                .setAction(cp -> cp.getFriends().setBlockingLogins(!cp.getFriends().isBlockingLogins())),
                        1, 1);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Game Notifications")
                                .setDisplayItem(Material.FEATHER, 1)
                                .setDescription("Receive friend game notifications"),
                        0, 2);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemToggle()
                                .setEnabledFun(cp -> !cp.getFriends().isBlockingGameNotifications())
                                .setAction(cp -> cp.getFriends().setBlockingGameNotifications(!cp.getFriends().isBlockingGameNotifications())),
                        1, 2);
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
