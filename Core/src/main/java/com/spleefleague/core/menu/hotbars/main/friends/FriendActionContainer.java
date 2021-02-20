package com.spleefleague.core.menu.hotbars.main.friends;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.coreapi.player.friends.FriendsList;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerJoinOther;
import org.bukkit.Material;

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
                    CoreOfflinePlayer friend = Core.getInstance().getPlayers().getOffline(UUID.fromString(cp.getMenu().getMenuTag("friendUuid", String.class)));
                    if (friend != null) {
                        builder.append(friend.getName());
                    }
                    builder.append(" - Friend Actions");
                    return builder.toString();
                });

        container.addStaticItem(InventoryMenuAPI.createItemDynamic()
                        .setName("Favorite Friend")
                        .setDescription(cp -> {
                            UUID uuid = UUID.fromString(cp.getMenu().getMenuTag("friendUuid", String.class));
                            CoreOfflinePlayer friend = Core.getInstance().getPlayers().getOffline(uuid);
                            FriendsList.FriendInfo info = cp.getFriends().getInfo(uuid);
                            if (info == null) return "";
                            return friend.getDisplayName() + " is currently " + (info.favorite ? ("favorited") : ("not favorited"));
                        })
                        .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.BRAIN_CORAL))
                        .setAction(cp -> {
                            CoreOfflinePlayer friend = Core.getInstance().getPlayers().getOffline(UUID.fromString(cp.getMenu().getMenuTag("friendUuid", String.class)));
                            if (friend != null) {
                                cp.getFriends().toggleFavorite(friend.getUniqueId());
                            }
                        })
                        .setCloseOnAction(false),
                2, 3);

        container.addStaticItem(InventoryMenuAPI.createItemStatic()
                        .setName("Remove Friend")
                        .setAction(cp -> {
                            CoreOfflinePlayer friend = Core.getInstance().getPlayers().getOffline(UUID.fromString(cp.getMenu().getMenuTag("friendUuid", String.class)));
                            if (friend != null) {
                                cp.getFriends().sendFriendRemove(friend);
                            }
                        }),
                3, 4);

        container.addStaticItem(InventoryMenuAPI.createItemStatic()
                        .setName("Join Friend")
                        .setAction(cp -> {
                            CoreOfflinePlayer friend = Core.getInstance().getPlayers().getOffline(UUID.fromString(cp.getMenu().getMenuTag("friendUuid", String.class)));
                            if (friend != null) {
                                Core.getInstance().sendPacket(new PacketSpigotPlayerJoinOther(cp.getUniqueId(), friend.getUniqueId()));
                            }
                        }),
                4, 3);
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
