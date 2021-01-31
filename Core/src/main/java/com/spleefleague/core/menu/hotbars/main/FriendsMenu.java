package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.menu.hotbars.main.friends.FriendActionContainer;
import com.spleefleague.core.menu.hotbars.main.friends.FriendOptionsMenu;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.friends.CoreFriendsList;
import com.spleefleague.core.util.TimeUtils;
import com.spleefleague.core.util.variable.Day;
import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FriendsMenu {

    private static InventoryMenuItem menuItem = null;

    private static String FRIEND_SEARCH = "friendSearch";

    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("&9&lFriends List")
                .setDisplayItem(Material.FEATHER, 1)
                .setDescription("View your friends list")
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
                .setDescription("Click to add a friend to your list.")
                .setAction(cp -> cp.getMenu().setInventoryMenuAnvil(InventoryMenuAPI.createAnvil()
                        .setTitle("Add Friend")
                        .setSuccessFunc(str -> Core.getInstance().getPlayers().get(str) != null)
                        .setAction((cp2, str) -> cp2.getFriends().addFriend(Core.getInstance().getPlayers().get(str)))
                        .setFailText("Invalid Player!")))
                .setCloseOnAction(false),
                6, 2);

        container.addStaticItem(FriendOptionsMenu.getItem(), 6, 3);

        container.setOpenAction((container2, cp) -> {
            container2.clearUnsorted();
            CoreFriendsList friends = cp.getFriends();
            if (friends.getAll().isEmpty()) {
                container2.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("You have no friends yet! :(")
                                .setDescription("")
                                .setAction(cp2 -> {
                                    friends.addFriend(Core.getInstance().getPlayers().getOffline("Blaezon"));
                                }),
                        2, 2);
            } else {
                String search = cp.getMenu().getMenuTag(FRIEND_SEARCH, String.class);
                List<CoreFriendsList.FriendInfo> friendInfoList = friends.getAllSorted(CoreFriendsList.FriendSortStyle.ALPHABETICAL);
                for (CoreFriendsList.FriendInfo info : friendInfoList) {
                    CorePlayer friend = Core.getInstance().getPlayers().getOffline(info.uuid);
                    if (search != null && !friend.getName().contains(search)) continue;
                    StringBuilder description = new StringBuilder();

                    if (!friend.isOnline()) {
                        description.append(Chat.DEFAULT + "Last online " + TimeUtils.gcdTimeToString(friend.getLastOnline()) + " ago.\n");
                    } else {
                        description.append(ChatColor.GREEN + "Online\n");
                    }

                    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(info.addedTime), Day.getTimeZone().toZoneId());
                    description.append(Chat.DEFAULT + "Friends since: " + zonedDateTime.format(DateTimeFormatter.ofPattern("dd MMM, uuuu")));

                    container2.addMenuItem(InventoryMenuAPI.createItemStatic()
                            .setName(friend.getRank().getColor() + "" + ChatColor.BOLD + friend.getName() + ChatColor.YELLOW + " ▹ Click for options ◃")
                            .setDisplayItem(InventoryMenuSkullManager.getPlayerSkull(info.uuid))
                            .setDescription(description.toString())
                            .setDescriptionBuffer(0)
                            .setAction(cp2 -> cp2.getMenu().setMenuTag("friendUuid", info.uuid))
                            .setLinkedContainer(FriendActionContainer.getItem()));
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
