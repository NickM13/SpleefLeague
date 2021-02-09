package com.spleefleague.core.menu.hotbars.main.party;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.player.friends.FriendsList;
import org.bukkit.Material;

import java.util.UUID;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class PartyActionContainer {

    private static InventoryMenuContainerChest container = null;

    public static void init() {
        container = InventoryMenuAPI.createContainer()
                .setTitle(cp -> {
                    StringBuilder builder = new StringBuilder();
                    CorePlayer member = Core.getInstance().getPlayers().getOffline(UUID.fromString(cp.getMenu().getMenuTag("partyUuid", String.class)));
                    if (member != null) {
                        builder.append(member.getName());
                    }
                    builder.append(" - Friend Actions");
                    return builder.toString();
                });

        container.addStaticItem(InventoryMenuAPI.createItemDynamic()
                        .setName("Promote Player")
                        .setDescription(cp -> {
                            UUID uuid = UUID.fromString(cp.getMenu().getMenuTag("partyUuid", String.class));
                            CorePlayer member = Core.getInstance().getPlayers().getOffline(uuid);
                            return "Transfer ownership of the party to " + member.getDisplayName();
                        })
                        .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.BRAIN_CORAL))
                        .setAction(cp -> {
                            CorePlayer member = Core.getInstance().getPlayers().getOffline(UUID.fromString(cp.getMenu().getMenuTag("partyUuid", String.class)));
                            if (member != null) {
                                cp.getFriends().toggleFavorite(member.getUniqueId());
                            }
                        })
                        .setCloseOnAction(false),
                2, 3);

        container.addStaticItem(InventoryMenuAPI.createItemStatic()
                        .setName("Kick Player")
                        .setAction(cp -> {
                            CorePlayer member = Core.getInstance().getPlayers().getOffline(UUID.fromString(cp.getMenu().getMenuTag("partyUuid", String.class)));
                            if (member != null) {
                                cp.getFriends().sendFriendRemove(member);
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
