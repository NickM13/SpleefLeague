package com.spleefleague.proxycore.infraction;

import com.mongodb.client.MongoCollection;
import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.infraction.InfractionManager;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import org.bson.Document;

import java.util.List;

/**
 * @author NickM13
 */
public class ProxyInfractionManager extends InfractionManager {

    private MongoCollection<Document> playerCollection;

    public void init() {
        playerCollection = ProxyCore.getInstance().getDatabase().getCollection("Players");
    }

    public void close() {

    }

    public void push(Infraction infraction) {
        Document findQuery = new Document("identifier", infraction.getTarget().toString());
        Document updateQuery = new Document("$push", infraction.toDocument());
        playerCollection.updateOne(findQuery, updateQuery);

        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(infraction.getTarget());
        if (pcp != null) {
            switch (infraction.getType()) {
                case BAN:

                    break;
                case KICK:

                    break;
                case WARNING:

                    break;
                case TEMPBAN:

                    break;
                case MUTE_PUBLIC:

                    break;
                case MUTE_SECRET:

                    break;
                case UNBAN:

                    break;
            }
        }
    }

}
