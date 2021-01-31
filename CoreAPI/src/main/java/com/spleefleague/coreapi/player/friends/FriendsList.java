package com.spleefleague.coreapi.player.friends;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;

import java.util.*;

/**
 * @author NickM13
 */
public abstract class FriendsList extends DBEntity {

    public static class FriendInfo extends DBEntity {

        @DBField public Long addedTime;
        @DBField public Boolean favorite = false;
        public UUID uuid;

        public FriendInfo(UUID uuid) {
            addedTime = System.currentTimeMillis();
            this.uuid = uuid;
        }

    }

    protected final Map<UUID, FriendInfo> friends = new HashMap<>();

    @DBField protected final Set<UUID> pending = new HashSet<>();
    @DBField protected final Set<UUID> requesting = new HashSet<>();

    @DBField protected boolean blockingRequests = false;
    @DBField protected boolean blockingLogins = false;
    @DBField protected boolean blockingGameNotifications = false;

    public FriendsList() {

    }

    public boolean isBlockingRequests() {
        return blockingRequests;
    }

    public void setBlockingRequests(boolean blockRequests) {
        this.blockingRequests = blockRequests;
    }

    public boolean isBlockingLogins() {
        return blockingLogins;
    }

    public void setBlockingLogins(boolean blockingLogins) {
        this.blockingLogins = blockingLogins;
    }

    public boolean isBlockingGameNotifications() {
        return blockingGameNotifications;
    }

    public void setBlockingGameNotifications(boolean blockingGameNotifications) {
        this.blockingGameNotifications = blockingGameNotifications;
    }

    public boolean isFriend(UUID uuid) {
        return friends.containsKey(uuid);
    }

    public Set<UUID> getRequesting() {
        return requesting;
    }

    public Set<UUID> getPending() {
        return pending;
    }

    @DBLoad(fieldName = "friends")
    protected void loadFriends(Document doc) {
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            FriendInfo info = new FriendInfo(uuid);
            info.load((Document) entry.getValue());
            friends.put(uuid, info);
        }
    }

}
