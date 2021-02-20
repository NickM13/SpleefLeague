package com.spleefleague.core.player.friends;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.variable.DBPlayer;
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
public class CoreFriendsList extends FriendsList {

    private final CoreOfflinePlayer owner;
    private final Set<String> allNames = new HashSet<>();
    private final Set<CoreOfflinePlayer> online = new HashSet<>();

    public CoreFriendsList(CoreOfflinePlayer owner) {
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

    public boolean canAddFriends() {
        CoreRank rank = Core.getInstance().getPlayers().get(owner.getUniqueId()).getRank();
        return rank.getMaxFriends() <= 0 || rank.getMaxFriends() > friends.size();
    }

    public int getCount() {
        return friends.size();
    }

    public void sendFriendRequest(CoreOfflinePlayer cp) {
        if (cp == null) return;
        if (cp.equals(owner)) {
            Core.getInstance().sendMessage(Core.getInstance().getPlayers().get(cp.getUniqueId()), ChatColor.RED + "You can't add yourself!");
            return;
        }
        if (canAddFriends()) {
            incoming.remove(cp.getUniqueId());
        }
        Core.getInstance().sendPacket(new PacketSpigotFriend(FriendsAction.ADD, owner.getUniqueId(), cp.getUniqueId()));
    }

    public void sendFriendRemove(CoreOfflinePlayer cp) {
        if (cp == null) return;
        if (!friends.containsKey(cp.getUniqueId())) {
            TextComponent text = new TextComponent();
            text.setColor(net.md_5.bungee.api.ChatColor.RED);
            text.addExtra("You aren't friends with ");
            text.addExtra(Core.getInstance().getPlayers().get(cp.getUniqueId()).getChatName());
            text.addExtra("!");
            Core.getInstance().sendMessage(Core.getInstance().getPlayers().get(cp.getUniqueId()), text);
            return;
        }
        friends.remove(cp.getUniqueId());
        Core.getInstance().sendPacket(new PacketSpigotFriend(FriendsAction.REMOVE, owner.getUniqueId(), cp.getUniqueId()));
    }

    public void sendFriendDecline(CoreOfflinePlayer cp) {
        incoming.remove(cp.getUniqueId());
        Core.getInstance().sendPacket(new PacketSpigotFriend(FriendsAction.DECLINE_INCOMING, owner.getUniqueId(), cp.getUniqueId()));
    }

    public void toggleFavorite(UUID uuid) {
        if (friends.containsKey(uuid)) {
            friends.get(uuid).favorite = !friends.get(uuid).favorite;
            Core.getInstance().sendPacket(new PacketSpigotFriend(
                    friends.get(uuid).favorite ? FriendsAction.FAVORITE : FriendsAction.UNFAVORITE,
                    owner.getUniqueId(),
                    uuid));
        }
    }

    public void setOnline(UUID uuid, CoreOfflinePlayer cp) {
        if (!friends.containsKey(uuid)) {
            return;
        }
        if (cp.getOnlineState() != DBPlayer.OnlineState.OFFLINE) {
            online.add(cp);
        } else {
            online.remove(cp);
        }
    }

    public Set<CoreOfflinePlayer> getOnline() {
        return online;
    }

    public void syncAdd(CoreOfflinePlayer target) {
        friends.put(target.getUniqueId(), new FriendInfo(target.getUniqueId()));
        allNames.add(target.getName());
        incoming.remove(target.getUniqueId());
        outgoing.remove(target.getUniqueId());
    }

    public void syncRemove(CoreOfflinePlayer target) {
        friends.remove(target.getUniqueId());
        allNames.remove(target.getName());
    }

    public void syncDeclineIncoming(CoreOfflinePlayer target) {
        incoming.remove(target.getUniqueId());
    }

    public void syncDeclineOutgoing(CoreOfflinePlayer target) {
        outgoing.remove(target.getUniqueId());
    }

    public void syncIncoming(CoreOfflinePlayer target) {
        incoming.add(target.getUniqueId());
    }

    public void syncOutgoing(CoreOfflinePlayer target) {
        outgoing.add(target.getUniqueId());
    }

    private static class FriendNameComparator implements Comparator<FriendInfo> {

        @Override
        public int compare(FriendInfo friendInfo1, FriendInfo friendInfo2) {
            CoreOfflinePlayer cp1 = Core.getInstance().getPlayers().getOffline(friendInfo1.uuid);
            CoreOfflinePlayer cp2 = Core.getInstance().getPlayers().getOffline(friendInfo2.uuid);
            return cp1.getName().compareTo(cp2.getName());
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

    public FriendInfo getInfo(UUID uuid) {
        return friends.get(uuid);
    }

}
