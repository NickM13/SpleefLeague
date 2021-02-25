package com.spleefleague.proxycore.player.friends;

import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.player.friends.FriendsAction;
import com.spleefleague.coreapi.player.friends.FriendsList;
import com.spleefleague.coreapi.utils.packet.bungee.friend.PacketBungeeFriend;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.chat.ProxyChat;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 */
public class ProxyFriendsList extends FriendsList {

    private final ProxyCorePlayer owner;
    private final Set<ProxyCorePlayer> online = new HashSet<>();

    public ProxyFriendsList(ProxyCorePlayer owner) {
        super();
        this.owner = owner;
        checkOnline();
    }

    public void checkOnline() {
        online.clear();
        for (UUID uuid : friends.keySet()) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            if (pcp != null) {
                online.add(pcp);
            }
        }
    }

    public void onPlayerJoin(ProxyCorePlayer pcp) {
        online.add(pcp);
    }

    public void onPlayerLeave(ProxyCorePlayer pcp) {
        online.remove(pcp);
    }

    public Set<ProxyCorePlayer> getOnline() {
        return online;
    }

    public Set<UUID> getAll() {
        return friends.keySet();
    }

    @DBSave(fieldName = "friends")
    private Document saveFriends() {
        Document doc = new Document();
        for (Map.Entry<UUID, FriendInfo> entry : friends.entrySet()) {
            doc.put(entry.getKey().toString(), entry.getValue().toDocument());
        }
        return doc;
    }

    public boolean canAddFriends() {
        return owner.getRank().getMaxFriends() <= 0 ||
                owner.getRank().getMaxFriends() > friends.size();
    }

    private void addFriend(ProxyCorePlayer pcp) {
        friends.put(pcp.getUniqueId(), new FriendInfo(pcp.getUniqueId()));
        outgoing.remove(pcp.getUniqueId());
        incoming.remove(pcp.getUniqueId());
        if (pcp.isOnline()) online.add(pcp);

        ProxyCore.getInstance().getPacketManager().sendPacket(owner.getUniqueId(), new PacketBungeeFriend(FriendsAction.ADD, owner.getUniqueId(), pcp.getUniqueId()));
    }

    public int receiveFriendRequest(ProxyCorePlayer pcp) {
        if (!canAddFriends()) {
            return 6;
        } else if (!owner.getOptions().getBoolean("Friend:Requests")) {
            return 4;
        } else if (friends.containsKey(pcp.getUniqueId())) {
            return 2;
        } else if (outgoing.contains(pcp.getUniqueId())) {
            addFriend(pcp);
            TextComponent text = new TextComponent();
            text.addExtra(pcp.getChatName());
            text.addExtra(" has accepted your friend request!");
            ProxyCore.getInstance().sendMessage(owner, text);
            return 0;
        } else if (!incoming.contains(pcp.getUniqueId())) {
            TextComponent text = new TextComponent("Friend request received from ");
            text.addExtra(pcp.getChatName());
            ProxyCore.getInstance().sendMessage(owner, text);

            ProxyChat.sendConfirmationButtons(owner, "/friend add " + pcp.getName(), "/friend decline " + pcp.getName());

            incoming.add(pcp.getUniqueId());

            PacketBungeeFriend packet =  new PacketBungeeFriend(FriendsAction.INCOMING,
                    owner.getUniqueId(),
                    pcp.getUniqueId());
            ProxyCore.getInstance().getPacketManager().sendPacket(owner.getUniqueId(), packet);
            return 1;
        } else {
            return 3;
        }
    }

    public void onFriendRequest(ProxyCorePlayer pcp) {
        TextComponent text;
        PacketBungeeFriend packet;
        if (friends.containsKey(pcp.getUniqueId())) {
            text = new TextComponent("You're already friends with ");
            text.addExtra(pcp.getChatName());
            ProxyCore.getInstance().sendMessageError(owner, text);
            packet =  new PacketBungeeFriend(FriendsAction.ADD,
                    owner.getUniqueId(),
                    pcp.getUniqueId());
            ProxyCore.getInstance().getPacketManager().sendPacket(owner.getUniqueId(), packet);
            return;
        }
        if (!canAddFriends()) {
            text = new TextComponent("Your friends list is full");
            ProxyCore.getInstance().sendMessageError(owner, text);
            return;
        }
        switch (pcp.getFriends().receiveFriendRequest(owner)) {
            case 0:
                addFriend(pcp);
                text = new TextComponent("You are now friends with ");
                text.addExtra(pcp.getChatName());
                text.addExtra("!");
                ProxyCore.getInstance().sendMessage(owner, text);
                break;
            case 1:
                text = new TextComponent("Friend request sent to ");
                text.addExtra(pcp.getChatName());
                ProxyCore.getInstance().sendMessage(owner, text);
                outgoing.add(pcp.getUniqueId());
                packet =  new PacketBungeeFriend(FriendsAction.OUTGOING,
                        owner.getUniqueId(),
                        pcp.getUniqueId());
                ProxyCore.getInstance().getPacketManager().sendPacket(owner.getUniqueId(), packet);
                break;
            case 2:
                text = new TextComponent("You're already friends with ");
                text.addExtra(pcp.getChatName());
                ProxyCore.getInstance().sendMessageError(owner, text);
                friends.put(pcp.getUniqueId(), new FriendInfo(pcp.getUniqueId()));
                packet =  new PacketBungeeFriend(FriendsAction.ADD,
                        owner.getUniqueId(),
                        pcp.getUniqueId());
                ProxyCore.getInstance().getPacketManager().sendPacket(owner.getUniqueId(), packet);
                break;
            case 3:
                text = new TextComponent("You already have an outgoing friend request to ");
                text.addExtra(pcp.getChatName());
                text.addExtra("!");
                ProxyCore.getInstance().sendMessage(owner, text);
                break;
            case 4:
                text = new TextComponent(pcp.getChatName());
                text.addExtra(" is blocking friend requests");
                ProxyCore.getInstance().sendMessageError(owner, text);
                break;
            case 6:
                text = new TextComponent("Their friends list is full");
                ProxyCore.getInstance().sendMessageError(owner, text);
                break;
        }
    }

    public void receiveFriendRemove(ProxyCorePlayer pcp) {
        if (friends.remove(pcp.getUniqueId()) != null) {
            PacketBungeeFriend packet =  new PacketBungeeFriend(FriendsAction.REMOVE,
                    owner.getUniqueId(),
                    pcp.getUniqueId());
            online.remove(pcp);
            TextComponent text = new TextComponent();
            text.addExtra(pcp.getChatName());
            text.addExtra(" has removed you as a friend!");
            ProxyCore.getInstance().sendMessage(owner, text);
            ProxyCore.getInstance().getPacketManager().sendPacket(owner.getUniqueId(), packet);
        }
    }

    public void onFriendRemove(ProxyCorePlayer pcp) {
        pcp.getFriends().receiveFriendRemove(owner);
        if (friends.containsKey(pcp.getUniqueId())) {
            TextComponent text = new TextComponent("You are no longer friends with ");
            text.addExtra(pcp.getChatName());
            ProxyCore.getInstance().sendMessage(owner, text);
            friends.remove(pcp.getUniqueId());
            online.remove(pcp);
            PacketBungeeFriend packet =  new PacketBungeeFriend(FriendsAction.REMOVE,
                    owner.getUniqueId(),
                    pcp.getUniqueId());
            ProxyCore.getInstance().getPacketManager().sendPacket(owner.getUniqueId(), packet);
        } else {
            TextComponent text = new TextComponent("You aren't friends with ");
            text.addExtra(pcp.getChatName());
            ProxyCore.getInstance().sendMessageError(owner, text);
        }
    }

    public void receiveFriendDecline(ProxyCorePlayer pcp) {
        if (outgoing.remove(pcp.getUniqueId())) {
            TextComponent text = new TextComponent();
            text.addExtra(pcp.getChatName());
            text.addExtra(" declined your friend request");
            ProxyCore.getInstance().sendMessage(owner, text);
            PacketBungeeFriend packet =  new PacketBungeeFriend(FriendsAction.DECLINE_OUTGOING,
                    owner.getUniqueId(),
                    pcp.getUniqueId());
            ProxyCore.getInstance().getPacketManager().sendPacket(owner.getUniqueId(), packet);
        }
    }

    public void onFriendDecline(ProxyCorePlayer pcp) {
        if (incoming.remove(pcp.getUniqueId())) {
            TextComponent text = new TextComponent("Declined friend request from ");
            text.addExtra(pcp.getChatName());
            ProxyCore.getInstance().sendMessage(owner, text);
            PacketBungeeFriend packet =  new PacketBungeeFriend(FriendsAction.DECLINE_INCOMING,
                    owner.getUniqueId(),
                    pcp.getUniqueId());
            ProxyCore.getInstance().getPacketManager().sendPacket(owner.getUniqueId(), packet);
        } else {
            TextComponent text = new TextComponent("You don't have a friend request from ");
            text.addExtra(pcp.getChatName());
            ProxyCore.getInstance().sendMessageError(owner, text);
        }
        pcp.getFriends().receiveFriendDecline(owner);
    }

    public void setFriendFavorite(ProxyCorePlayer pcp, boolean favorited) {
        if (friends.containsKey(pcp.getUniqueId())) {
            friends.get(pcp.getUniqueId()).favorite = favorited;
        }
    }

}
