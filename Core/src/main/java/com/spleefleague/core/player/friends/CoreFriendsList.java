package com.spleefleague.core.player.friends;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.player.friends.FriendsList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

/**
 * @author NickM13
 */
public class CoreFriendsList extends FriendsList {

    private final Set<String> allNames = new HashSet<>();
    private final Set<CorePlayer> online = new HashSet<>();

    public CoreFriendsList() {

    }

    public int addFriend(CorePlayer cp) {
        if (!cp.isOnline()) return 1;
        if (isFull()) return 2;
        requesting.remove(cp.getUniqueId());
        pending.remove(cp.getUniqueId());
        friends.put(cp.getUniqueId(), new FriendInfo(cp.getUniqueId()));
        allNames.add(cp.getName());
        TextComponent text = new TextComponent();
        text.addExtra("You are now friends with ");
        text.addExtra(cp.getChatName());
        Core.getInstance().sendMessage(owner, text);
        return 0;
    }

    public void declineFriend(CorePlayer cp) {
        if (pending.remove(cp.getUniqueId())) {
            TextComponent text = new TextComponent();
            text.addExtra("Declined friend request from ");
            text.addExtra(cp.getChatName());
            Core.getInstance().sendMessage(owner, text);
        }
        if (cp.isLocal()) {
            cp.getFriends().removeFriend(owner);
        } else {
            Core.getInstance().sendPacket(new PacketFriendSpigot(PacketFriendSpigot.FriendType.REMOVE, owner.getUniqueId(), cp.getUniqueId()));
        }
    }

    public void removeFriend(CorePlayer cp) {
        if (receiveFriendRemove(cp)) {
            TextComponent text = new TextComponent();
            text.addExtra("You are no longer friends with ");
            text.addExtra(cp.getChatName());
            Core.getInstance().sendMessage(owner, text);
            if (!cp.isOnline()) {
                cp.getFriends().removeFriend(owner);
            } else {
                Core.getInstance().sendPacket(new PacketFriendSpigot(PacketFriendSpigot.FriendType.REMOVE, owner.getUniqueId(), cp.getUniqueId()));
            }
        } else {
            /*
            TextComponent text = new TextComponent();
            text.addExtra("You weren't friends with ");
            text.addExtra(cp.getChatName());
            text.addExtra("!");
            Core.getInstance().sendMessage(owner, text);
            */
        }
    }

    public void sendFriendRequest(CorePlayer cp) {
        TextComponent text;
        if (friends.containsKey(cp.getUniqueId())) {
            text = new TextComponent();
            text.setColor(ChatColor.RED);
            text.addExtra("You're already friends with ");
            text.addExtra(cp.getChatName());
            text.addExtra("!");
            Core.getInstance().sendMessage(owner, text);
            return;
        }
        if (requesting.contains(cp.getUniqueId())) {
            switch (addFriend(cp)) {
                case 0:
                    requesting.remove(cp.getUniqueId());
                    pending.remove(cp.getUniqueId());
                    if (cp.isLocal()) {
                        cp.getFriends().receiveFriendRequest(owner);
                    } else {
                        Core.getInstance().sendPacket(new PacketFriendSpigot(PacketFriendSpigot.FriendType.ADD, owner.getUniqueId(), cp.getUniqueId()));
                    }
                    break;
                case 1:
                    Core.getInstance().sendMessage(owner, ChatColor.RED + "They aren't online!");
                    break;
                case 2:
                    Core.getInstance().sendMessage(owner, ChatColor.RED + "Your friends list is full!");
                    break;
                default: break;
            }
        } else {
            if (!pending.contains(cp.getUniqueId())) {
                if (isFull()) {
                    Core.getInstance().sendMessage(owner, ChatColor.RED + "Your friends list is full!");
                } else if (cp.getFriends().isBlockingRequests()) {
                    text = new TextComponent();
                    text.setColor(ChatColor.RED);
                    text.addExtra(cp.getChatName());
                    text.addExtra(" has friend requests disabled!");
                    Core.getInstance().sendMessage(owner, text);
                    return;
                } else if (!cp.getFriends().isFull()){
                    text = new TextComponent();
                    text.addExtra("Friend request sent to ");
                    text.addExtra(cp.getChatName());
                    text.addExtra("!");
                    Core.getInstance().sendMessage(owner, text);
                } else {
                    text = new TextComponent();
                    text.setColor(ChatColor.RED);
                    text.addExtra(cp.getChatNamePossessive());
                    text.addExtra(" friends list is full!");
                    Core.getInstance().sendMessage(owner, text);
                }
            } else {
                text = new TextComponent();
                text.setColor(ChatColor.RED);
                text.addExtra("You already have a pending friend request with ");
                text.addExtra(cp.getChatName());
                Core.getInstance().sendMessage(owner, text);
            }
            if (cp.isLocal()) {
                cp.getFriends().receiveFriendRequest(owner);
            } else {
                Core.getInstance().sendPacket(new PacketFriendSpigot(PacketFriendSpigot.FriendType.ADD, owner.getUniqueId(), cp.getUniqueId()));
            }
            pending.add(cp.getUniqueId());
        }
    }

    public boolean isFull() {
        return owner.getRank().getMaxFriends() > 0 && friends.size() >= owner.getRank().getMaxFriends();
    }

    public void receiveFriendRequest(CorePlayer cp) {
        if (friends.containsKey(cp.getUniqueId())) {
            return;
        }
        if (pending.contains(cp.getUniqueId())) {
            if (addFriend(cp) != 0) {
                // Friends list probably full!
                if (cp.isLocal()) {
                    cp.getFriends().receiveFriendRequest(owner);
                } else {
                    Core.getInstance().sendPacket(new PacketFriendSpigot(PacketFriendSpigot.FriendType.REMOVE, owner.getUniqueId(), cp.getUniqueId()));
                }
            }
            pending.remove(cp.getUniqueId());
        } else if (!requesting.contains(cp.getUniqueId())) {
            requesting.add(cp.getUniqueId());
            TextComponent text = new TextComponent();
            text.addExtra("Friend request received from ");
            text.addExtra(cp.getChatName());
            Core.getInstance().sendMessage(owner, text);
            Chat.sendConfirmationButtons(owner, "/friend add " + cp.getName(), "/friend decline " + cp.getUniqueId());
        }
    }

    public boolean receiveFriendRemove(CorePlayer cp) {
        requesting.remove(cp.getUniqueId());
        pending.remove(cp.getUniqueId());
        if (friends.remove(cp.getUniqueId()) != null) {
            allNames.remove(cp.getName());
            return true;
        }
        return false;
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

    public Set<UUID> getAll() {
        return friends.keySet();
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

    public Set<String> getNames() {
        return allNames;
    }

    public Set<UUID> getRequesting() {
        return requesting;
    }

    public Set<UUID> getPending() {
        return pending;
    }

}
