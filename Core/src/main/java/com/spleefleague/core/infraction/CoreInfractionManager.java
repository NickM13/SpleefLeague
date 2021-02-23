package com.spleefleague.core.infraction;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.infraction.InfractionManager;
import com.spleefleague.coreapi.infraction.InfractionType;
import org.bson.Document;

import java.util.UUID;

/**
 * @author NickM13
 * @since 2/22/2021
 */
public class CoreInfractionManager extends InfractionManager {

    private MongoCollection<Document> infractionCollection;

    public void init() {
        infractionCollection = Core.getInstance().getPluginDB().getCollection("Infractions");
    }

    public void close() {

    }

    public Infraction getMute(UUID uuid) {
        Document findQuery = new Document("identifier", uuid.toString());
        Document doc = infractionCollection.find(findQuery).first();
        if (doc == null) return null;

        Document latestDoc = doc.get("latest", Document.class);

        if (latestDoc == null) return null;

        Document recentDoc = latestDoc.get(InfractionType.MUTE_PUBLIC.getLatestId(), Document.class);

        if (recentDoc == null) {
            return null;
        }

        Infraction infraction = new Infraction();
        infraction.load(recentDoc);
        return infraction;
    }

}
