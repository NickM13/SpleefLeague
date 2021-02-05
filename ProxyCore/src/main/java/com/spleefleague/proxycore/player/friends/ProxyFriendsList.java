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

    public ProxyFriendsList(ProxyCorePlayer owner) {
        super();
        this.owner = owner;
    }

    public Set<ProxyCorePlayer> getOnline() {
        return new HashSet<>();
    }

    public void addFriend(UUID uuid) {
        friends.put(uuid, new FriendInfo(uuid));
    }

    public void removeFriend(UUID uuid) {
        friends.remove(uuid);
    }

    @DBSave(fieldName = "friends")
    private Document saveFriends() {
        Document doc = new Document();
        for (Map.Entry<UUID, FriendInfo> entry : friends.entrySet()) {
            doc.put(entry.getKey().toString(), entry.getValue().toDocument());
        }
        return doc;
    }

    private void addFriend(ProxyCorePlayer pcp) {
        friends.put(pcp.getUniqueId(), new FriendInfo(pcp.getUniqueId()));
        outgoing.remove(pcp.getUniqueId());
        incoming.remove(pcp.getUniqueId());

        ProxyCore.getInstance().getPacketManager().sendPacket(owner, new PacketBungeeFriend(FriendsAction.ADD, owner.getUniqueId(), pcp.getUniqueId()));
    }

    public int receiveFriendRequest(ProxyCorePlayer pcp) {
        if (!owner.getOptions().getBoolean("Friend:Requests")) {
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
            ProxyCore.getInstance().getPacketManager().sendPacket(owner, packet);
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
            ProxyCore.getInstance().getPacketManager().sendPacket(owner, packet);
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
                ProxyCore.getInstance().getPacketManager().sendPacket(owner, packet);
                break;
            case 2:
                text = new TextComponent("You're already friends with ");
                text.addExtra(pcp.getChatName());
                ProxyCore.getInstance().sendMessageError(owner, text);
                friends.put(pcp.getUniqueId(), new FriendInfo(pcp.getUniqueId()));
                packet =  new PacketBungeeFriend(FriendsAction.ADD,
                        owner.getUniqueId(),
                        pcp.getUniqueId());
                ProxyCore.getInstance().getPacketManager().sendPacket(owner, packet);
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
        }
    }

    public void receiveFriendRemove(ProxyCorePlayer pcp) {
        if (friends.remove(pcp.getUniqueId()) != null) {
            PacketBungeeFriend packet =  new PacketBungeeFriend(FriendsAction.REMOVE,
                    owner.getUniqueId(),
                    pcp.getUniqueId());
            ProxyCore.getInstance().getPacketManager().sendPacket(owner, packet);
        }
    }

    public void onFriendRemove(ProxyCorePlayer pcp) {
        pcp.getFriends().receiveFriendRemove(owner);
        if (friends.containsKey(pcp.getUniqueId())) {
            TextComponent text = new TextComponent("You are no longer friends with ");
            text.addExtra(pcp.getChatName());
            ProxyCore.getInstance().sendMessage(owner, text);
            friends.remove(pcp.getUniqueId());
            PacketBungeeFriend packet =  new PacketBungeeFriend(FriendsAction.REMOVE,
                    owner.getUniqueId(),
                    pcp.getUniqueId());
            ProxyCore.getInstance().getPacketManager().sendPacket(owner, packet);
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
            ProxyCore.getInstance().getPacketManager().sendPacket(owner, packet);
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
            ProxyCore.getInstance().getPacketManager().sendPacket(owner, packet);
        } else {
            TextComponent text = new TextComponent("You don't have a friend request from ");
            text.addExtra(pcp.getChatName());
            ProxyCore.getInstance().sendMessageError(owner, text);
        }
        pcp.getFriends().receiveFriendDecline(owner);
    }

}
