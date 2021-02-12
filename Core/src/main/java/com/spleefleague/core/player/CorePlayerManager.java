package com.spleefleague.core.player;

import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import org.bson.Document;

import java.util.*;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class CorePlayerManager extends PlayerManager<CorePlayer> {

    public CorePlayerManager(MongoCollection<Document> collection) {
        super(Core.getInstance(), CorePlayer.class, collection);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void resync(UUID uuid, List<PacketBungeePlayerResync.Field> fields) {
        if (!onlinePlayerListAll.containsKey(uuid)) {
            CoreLogger.logWarning("Attempted to reload field of offline player");
            return;
        }
        CorePlayer cp = onlinePlayerListAll.get(uuid);
        Document doc = playerCol.find(new Document("identifier", cp.getIdentifier())).first();
        for (PacketBungeePlayerResync.Field field : fields) {
            switch (field) {
                case RANK:
                    cp.reloadField(doc, Sets.newHashSet("permRank", "tempRanks"));
                    cp.updateRank();
                    break;
                default:
                    cp.reloadField(doc, Sets.newHashSet(field.getFieldName()));
                    break;
            }
        }
    }

}
