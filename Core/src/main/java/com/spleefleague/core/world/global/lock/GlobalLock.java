package com.spleefleague.core.world.global.lock;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import org.bson.Document;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/12/2020
 */
public class GlobalLock {

    private static final Set<BlockPosition> lockedBlocks = new HashSet<>();
    private static MongoCollection<Document> globalLockCol;

    public static void init() {
        globalLockCol = Core.getInstance().getPluginDB().getCollection("Locks");
        for (Document doc : globalLockCol.find()) {
            lockedBlocks.add(new BlockPosition(doc.get("x", Integer.class), doc.get("y", Integer.class), doc.get("z", Integer.class)));
        }
    }

    protected static void save() {

    }

    public static boolean lock(BlockPosition pos) {
        if (!lockedBlocks.contains(pos)) {
            globalLockCol.insertOne(new Document("x", pos.getX()).append("y", pos.getY()).append("z", pos.getZ()));
            lockedBlocks.add(pos);
            return true;
        }
        return false;
    }

    public static boolean unlock(BlockPosition pos) {
        Document query = new Document("x", pos.getX()).append("y", pos.getY()).append("z", pos.getZ());
        if (globalLockCol.find(query).first() != null) {
            globalLockCol.deleteMany(query);
            lockedBlocks.remove(pos);
            return true;
        }
        return false;
    }

    public static boolean isLocked(BlockPosition pos) {
        return lockedBlocks.contains(pos);
    }

}
