package com.spleefleague.core.menu.hotbars.main.friends;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import org.bukkit.Material;

public class FriendOptionsMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = InventoryMenuAPI.createItemStatic()
                .setName("Friend Settings")
                .setDisplayItem(Material.REDSTONE, 1)
                .setDescription("Manage your preferences on all things related to your friends!")
                .createLinkedContainer("Friend Settings");

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Friend Requests")
                                .setDisplayItem(Material.FEATHER, 1)
                                .setDescription("Allow incoming friend requests"),
                        0, 0);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemToggle()
                                .setEnabledFun(cp -> cp.getOptions().getBoolean("Friend:Requests"))
                                .setAction(cp -> cp.getOptions().toggle("Friend:Requests")),
                        1, 0);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Friend Logins")
                                .setDisplayItem(Material.FEATHER, 4)
                                .setDescription("Receive friend login notifications"),
                        0, 1);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemToggle()
                                .setEnabledFun(cp -> cp.getOptions().getBoolean("Friend:Connection"))
                                .setAction(cp -> cp.getOptions().toggle("Friend:Connection")),
                        1, 1);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Game Notifications")
                                .setDisplayItem(Material.FEATHER, 5)
                                .setDescription("Receive friend game notifications"),
                        0, 2);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemToggle()
                                .setEnabledFun(cp -> cp.getOptions().getBoolean("Friend:Notifications"))
                                .setAction(cp -> cp.getOptions().toggle("Friend:Notifications")),
                        1, 2);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Non-Friend Messages")
                                .setDisplayItem(Material.BIRCH_SIGN, 1)
                                .setDescription("Receive messages from non-friends"),
                        0, 3);

        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItemToggle()
                                .setEnabledFun(cp -> cp.getOptions().getBoolean("Friend:Messages"))
                                .setAction(cp -> cp.getOptions().toggle("Friend:Messages")),
                        1, 3);
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
