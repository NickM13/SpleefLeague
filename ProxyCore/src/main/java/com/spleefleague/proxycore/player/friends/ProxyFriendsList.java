package com.spleefleague.proxycore.player.friends;

import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.player.friends.FriendsList;
import org.bson.Document;

import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 */
public class ProxyFriendsList extends FriendsList {

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

}
