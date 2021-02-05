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

    @DBField protected final Set<UUID> outgoing = new HashSet<>();
    @DBField protected final Set<UUID> incoming = new HashSet<>();

    public FriendsList() {

    }
    
    public boolean isFriend(UUID uuid) {
        return friends.containsKey(uuid);
    }

    public Set<UUID> getIncoming() {
        return incoming;
    }

    public Set<UUID> getOutgoing() {
        return outgoing;
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
