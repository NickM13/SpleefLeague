package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.menu.hotbars.main.friends.FriendActionContainer;
import com.spleefleague.core.menu.hotbars.main.friends.FriendOptionsMenu;
import com.spleefleague.core.menu.hotbars.main.friends.FriendPendingMenu;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.friends.CorePlayerFriends;
import com.spleefleague.core.util.TimeUtils;
import com.spleefleague.core.util.variable.Day;
import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class FriendsMenu {

    private static InventoryMenuItem menuItem = null;

    private static String FRIEND_SEARCH = "friendSearch";

    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("&9&lFriends List")
                .setDisplayItem(Material.FEATHER, 1)
                .setDescription(cp -> "View your friends list, change friends settings and more!\n\n" +
                        cp.getFriends().getCount() + "/" + cp.getRank().getMaxFriends())
                .createLinkedContainer("Friends");

        InventoryMenuContainerChest container = menuItem.getLinkedChest();

        container.addStaticItem(InventoryMenuAPI.createItemSearch()
                        .setName("Search for Friend")
                        .setSearchTag(FRIEND_SEARCH)
                        .build(),
                2, 0);

        container.addStaticItem(InventoryMenuAPI.createItemStatic()
                .setName("Add Friend")
                .setDisplayItem(Material.FEATHER, 1)
                .setDescription("Send a new friend request to a specific player.")
                .setAction(cp -> cp.getMenu().setInventoryMenuAnvil(InventoryMenuAPI.createAnvil()
                        .setTitle("Add Friend")
                        .setSuccessFunc(str -> Core.getInstance().getPlayers().get(str) != null)
                        .setAction((cp2, str) -> cp2.getFriends().sendFriendRequest(Core.getInstance().getPlayers().get(str)))
                        .setFailText("Invalid Player!")))
                .setCloseOnAction(false),
                6, 2);

        container.addStaticItem(FriendPendingMenu.getItem(), 6, 0);

        container.addStaticItem(FriendOptionsMenu.getItem(), 6, 3);

        InventoryMenuContainer actionContainer = FriendActionContainer.getItem()
                .setTitle(cp -> {
                    StringBuilder builder = new StringBuilder();
                    CorePlayer friend = Core.getInstance().getPlayers().getOffline(UUID.fromString(cp.getMenu().getMenuTag("friendUuid", String.class)));
                    if (friend != null) {
                        builder.append(friend.getName());
                    }
                    builder.append(" - Friend Actions");
                    return builder.toString();
                })
                .setParent(container);

        container.setOpenAction((container2, cp) -> {
            container2.clearUnsorted();
            CorePlayerFriends friends = cp.getFriends();
            if (friends.getAllNames().isEmpty()) {
                container2.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("You have no friends yet! :(")
                                .setDescription("")
                        // UUID is for MrWired
                                .setAction(cp2 -> friends.sendFriendRemove(Core.getInstance().getPlayers().getOffline(UUID.fromString("62d31d56-c863-4b17-a7b2-eae837e77e1a")))),
                        2, 2);
            } else {
                String search = cp.getMenu().getMenuTag(FRIEND_SEARCH, String.class);
                List<CorePlayerFriends.FriendInfo> friendInfoList = friends.getAllSorted(CorePlayerFriends.FriendSortStyle.ALPHABETICAL);
                for (CorePlayerFriends.FriendInfo info : friendInfoList) {
                    CorePlayer friend = Core.getInstance().getPlayers().getOffline(info.uuid);
                    if (search != null && !friend.getName().contains(search)) continue;
                    StringBuilder description = new StringBuilder();

                    if (!friend.isOnline()) {
                        description.append(ChatColor.GOLD + "Last online ").append(TimeUtils.gcdTimeToString(System.currentTimeMillis() - friend.getLastOnline())).append(" ago.\n");
                    } else {
                        description.append(ChatColor.GREEN + "Online\n");
                    }

                    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(info.addedTime), Day.getTimeZone().toZoneId());
                    description.append(ChatColor.GOLD + "Friends since: ").append(zonedDateTime.format(DateTimeFormatter.ofPattern("MMMM, uuuu")));

                    container2.addMenuItem(InventoryMenuAPI.createItemStatic()
                            .setName(friend.getRank().getColor() + "" + ChatColor.BOLD + friend.getName() + ChatColor.YELLOW + " ▹ Click for options ◃")
                            .setDisplayItem(InventoryMenuSkullManager.getPlayerSkull(info.uuid))
                            .setDescription(description.toString())
                            .setDescriptionBuffer(0)
                            .setAction(cp2 -> cp2.getMenu().setMenuTag("friendUuid", info.uuid.toString()))
                            .setLinkedContainer(actionContainer));
                }
            }
            cp.getMenu().removeMenuTag(FRIEND_SEARCH);
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