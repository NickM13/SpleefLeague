package com.spleefleague.core.menu.hotbars.main.friends;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.player.CorePlayer;

import java.util.UUID;

/**
 * @author NickM13
 */
public class FriendActionContainer {

    private static InventoryMenuContainerChest container = null;

    public static void init() {
        container = InventoryMenuAPI.createContainer()
                .setTitle(cp -> {
                    StringBuilder builder = new StringBuilder();
                    CorePlayer friend = Core.getInstance().getPlayers().getOffline(UUID.fromString(cp.getMenu().getMenuTag("friendUuid", String.class)));
                    if (friend != null) {
                        builder.append(friend.getName());
                    }
                    builder.append(" - Friend Actions");
                    return builder.toString();
                });

        container.addStaticItem(InventoryMenuAPI.createItemStatic()
                        .setName("Remove Friend")
                        .setAction(cp -> {
                            CorePlayer friend = Core.getInstance().getPlayers().getOffline(UUID.fromString(cp.getMenu().getMenuTag("friendUuid", String.class)));
                            if (friend != null) {
                                cp.getFriends().sendFriendRemove(friend);
                            }
                        }),
                3, 4);
    }

    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuContainerChest getItem() {
        if (container == null) init();
        return container;
    }

}
