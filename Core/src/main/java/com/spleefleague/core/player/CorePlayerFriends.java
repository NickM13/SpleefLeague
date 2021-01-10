package com.spleefleague.core.player;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.coreapi.database.variable.DBVariable;
import com.spleefleague.coreapi.utils.packet.spigot.PacketFriendSpigot;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author NickM13
 */
public class CorePlayerFriends extends DBVariable<Document> {

    private CorePlayer owner;

    public static class FriendInfo extends DBEntity {

        @DBField public Long addedTime;

        public FriendInfo() {
            addedTime = System.currentTimeMillis();
        }

    }

    private final Map<UUID, FriendInfo> friends = new HashMap<>();
    private final Set<String> allNames = new HashSet<>();
    private final Set<UUID> online = new HashSet<>();
    private final Set<UUID> pending = new HashSet<>();
    private final Set<UUID> requesting = new HashSet<>();

    public CorePlayerFriends() {

    }

    public void setOwner(CorePlayer owner) {
        this.owner = owner;
    }

    public boolean isFriend(UUID uuid) {
        return friends.containsKey(uuid);
    }

    public void addFriend(CorePlayer cp) {
        friends.put(cp.getUniqueId(), new FriendInfo());
        Chat.sendMessageToPlayerSuccess(owner, "You are now friends with " + cp.getName() + "!");
    }

    public void removeFriend(CorePlayer cp) {
        if (receiveFriendRemove(cp)) {
            Chat.sendMessageToPlayerSuccess(owner, "You are no longer friends with " + cp.getDisplayName());
        }
        if (!cp.isOnline()) {
            cp.getFriends().removeFriend(owner);
            Core.getInstance().getPlayers().save(cp);
        } else {
            Core.getInstance().sendPacket(new PacketFriendSpigot(PacketFriendSpigot.FriendType.REMOVE, owner.getUniqueId(), cp.getUniqueId()));
        }
    }

    public void sendFriendRequest(CorePlayer cp) {
        if (requesting.contains(cp.getUniqueId())) {
            addFriend(cp);
            requesting.remove(cp.getUniqueId());
            Core.getInstance().sendPacket(new PacketFriendSpigot(PacketFriendSpigot.FriendType.ADD, owner.getUniqueId(), cp.getUniqueId()));
        } else {
            Chat.sendMessageToPlayerInfo(owner, "Friend request sent to " + cp.getName() + "");
            Core.getInstance().sendPacket(new PacketFriendSpigot(PacketFriendSpigot.FriendType.ADD, owner.getUniqueId(), cp.getUniqueId()));
            pending.add(cp.getUniqueId());
        }
    }

    public void receiveFriendRequest(CorePlayer cp) {
        if (pending.contains(cp.getUniqueId())) {
            addFriend(cp);
            pending.remove(cp.getUniqueId());
        } else if (!requesting.contains(cp.getUniqueId())) {
            requesting.add(cp.getUniqueId());
            Chat.sendMessageToPlayerInfo(owner, "Friend request from " + cp.getName() + "!");
            Chat.sendMessageToPlayerInfo(owner, "To accept, type /friend add " + cp.getName());
        }
    }

    public boolean receiveFriendRemove(CorePlayer cp) {
        requesting.remove(cp.getUniqueId());
        pending.remove(cp.getUniqueId());
        return friends.remove(cp.getUniqueId()) != null;
    }

    public void setOnline(UUID uuid, boolean online) {
        if (!friends.containsKey(uuid)) {
            return;
        }
        if (online) {
            this.online.add(uuid);
        } else {
            this.online.remove(uuid);
        }
    }

    public Set<UUID> getOnline() {
        return online;
    }

    public Set<UUID> getAll() {
        return friends.keySet();
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

    @Override
    public void load(Document doc) {
        pending.clear();
        requesting.clear();
        friends.clear();

        if (doc.containsKey("pending")) pending.addAll(doc.getList("pending", String.class).stream().map(UUID::fromString).collect(Collectors.toList()));
        if (doc.containsKey("requesting")) requesting.addAll(doc.getList("requesting", String.class).stream().map(UUID::fromString).collect(Collectors.toList()));

        if (doc.containsKey("friends")) {
            for (Map.Entry<String, Object> entry : doc.get("friends", Document.class).entrySet()) {
                FriendInfo info = new FriendInfo();
                info.load((Document) entry.getValue());
                UUID uuid = UUID.fromString(entry.getKey());
                friends.put(uuid, info);
                allNames.add(Bukkit.getOfflinePlayer(uuid).getName());
            }
        }
    }

    @Override
    public Document save() {
        Document doc = new Document();

        doc.append("pending", pending.stream().map(UUID::toString).collect(Collectors.toList()));
        doc.append("requesting", requesting.stream().map(UUID::toString).collect(Collectors.toList()));

        Document friendsDoc = new Document();
        for (Map.Entry<UUID, FriendInfo> entry : friends.entrySet()) {
            friendsDoc.put(entry.getKey().toString(), entry.getValue().toDocument());
        }
        doc.append("friends", friendsDoc);

        return doc;
    }

}
