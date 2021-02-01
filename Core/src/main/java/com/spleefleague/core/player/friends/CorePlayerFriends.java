package com.spleefleague.core.player.friends;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.friends.FriendsAction;
import com.spleefleague.coreapi.player.friends.FriendsList;
import com.spleefleague.coreapi.utils.packet.spigot.friend.PacketSpigotFriend;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.List;

/**
 * @author NickM13
 */
public class CorePlayerFriends extends FriendsList {

    private final CorePlayer owner;
    private final Set<String> allNames = new HashSet<>();
    private final Set<CorePlayer> online = new HashSet<>();

    public CorePlayerFriends(CorePlayer owner) {
        this.owner = owner;
    }

    @Override
    public void afterLoad() {
        super.afterLoad();
        for (UUID uuid : friends.keySet()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            if (op.isOnline()) {
                online.add(Core.getInstance().getPlayers().get(uuid));
            }
            allNames.add(op.getName());
        }
    }

    public void sendFriendRequest(CorePlayer cp) {
        if (cp == null) return;
        if (cp.equals(owner)) {
            Core.getInstance().sendMessage(owner, ChatColor.RED + "You can't add yourself!");
            return;
        }
        Core.getInstance().sendPacket(new PacketSpigotFriend(FriendsAction.ADD, owner.getUniqueId(), cp.getUniqueId()));
    }

    public void sendFriendRemove(CorePlayer cp) {
        if (cp == null) return;
        if (!friends.containsKey(cp.getUniqueId())) {
            TextComponent text = new TextComponent();
            text.setColor(net.md_5.bungee.api.ChatColor.RED);
            text.addExtra("You aren't friends with ");
            text.addExtra(cp.getChatName());
            text.addExtra("!");
            Core.getInstance().sendMessage(cp, text);
            return;
        }
        Core.getInstance().sendPacket(new PacketSpigotFriend(FriendsAction.REMOVE, owner.getUniqueId(), cp.getUniqueId()));
    }

    public void sendFriendDecline(CorePlayer cp) {
        Core.getInstance().sendPacket(new PacketSpigotFriend(FriendsAction.DECLINE, owner.getUniqueId(), cp.getUniqueId()));
    }

    public void setOnline(UUID uuid, CorePlayer cp) {
        if (!friends.containsKey(uuid)) {
            return;
        }
        if (cp.isOnline()) {
            online.add(cp);
        } else {
            online.remove(cp);
        }
    }

    public Set<CorePlayer> getOnline() {
        return online;
    }

    private static class FriendNameComparator implements Comparator<FriendInfo> {

        @Override
        public int compare(FriendInfo friendInfo1, FriendInfo friendInfo2) {
            CorePlayer cp1 = Core.getInstance().getPlayers().getOffline(friendInfo1.uuid);
            CorePlayer cp2 = Core.getInstance().getPlayers().getOffline(friendInfo2.uuid);
            return cp1.getNickname().compareTo(cp2.getNickname());
        }

    }

    private static class FriendOnlineComparator implements Comparator<FriendInfo> {

        @Override
        public int compare(FriendInfo friendInfo1, FriendInfo friendInfo2) {
            return Boolean.compare(Core.getInstance().getPlayers().isOnline(friendInfo1.uuid), Core.getInstance().getPlayers().isOnline(friendInfo2.uuid));
        }

    }

    private static class FriendAddTimeComparator implements Comparator<FriendInfo> {

        @Override
        public int compare(FriendInfo friendInfo1, FriendInfo friendInfo2) {
            return friendInfo1.addedTime.compareTo(friendInfo2.addedTime);
        }

    }

    public enum FriendSortStyle {
        ALPHABETICAL(new FriendNameComparator()),
        ONLINE(new FriendOnlineComparator()),
        ADD_TIME(new FriendAddTimeComparator());

        Comparator<FriendInfo> comparator;

        FriendSortStyle(Comparator<FriendInfo> comparator) {
            this.comparator = comparator;
        }

        public Comparator<FriendInfo> getComparator() {
            return comparator;
        }

    }

    public List<FriendInfo> getAllSorted(FriendSortStyle... sortStyles) {
        List<FriendInfo> sorted = new ArrayList<>(friends.values());

        for (FriendSortStyle sortStyle : sortStyles) {
            sorted.sort(sortStyle.getComparator());
        }

        return sorted;
    }

    public Set<String> getAllNames() {
        return allNames;
    }

}
