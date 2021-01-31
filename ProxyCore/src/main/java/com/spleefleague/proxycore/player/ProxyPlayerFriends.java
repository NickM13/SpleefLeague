package com.spleefleague.proxycore.player;

import com.spleefleague.coreapi.database.annotation.DBLoad;
import org.bson.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Minimal friends list, only updated when a player logs in
 *
 * @author NickM13
 */
public class ProxyPlayerFriends {

    private Set<UUID> friends = new HashSet<>();

    public ProxyPlayerFriends() {

    }

    public Set<UUID> getFriends() {
        return friends;
    }

    @DBLoad(fieldName = "friends")
    public void loadFriends(Document doc) {
        friends.clear();
        if (doc == null) return;
        Document friendsDoc = doc.get("friends", Document.class);
        if (friendsDoc != null) {
            for (Map.Entry<String, Object> entry : friendsDoc.entrySet()) {
                friends.add(UUID.fromString(entry.getKey()));
            }
        }
    }

}
