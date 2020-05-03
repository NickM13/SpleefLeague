package com.spleefleague.core.world.build;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import org.bson.Document;

import java.util.*;

/**
 * @author NickM13
 * @since 4/26/2020
 */
public class BuildStructures {
    
    private static final SortedMap<String, BuildStructure> STRUCTURES = new TreeMap<>();
    private static MongoCollection<Document> fieldCol;
    
    public static void init() {
        fieldCol = Core.getInstance().getPluginDB().getCollection("Structures");
        for (Document document : fieldCol.find()) {
            BuildStructure buildStructure = new BuildStructure();
            buildStructure.load(document);
            STRUCTURES.put(buildStructure.getName(), buildStructure);
        }
    }
    
    public static boolean create(CorePlayer sender, String structureName) {
        if (STRUCTURES.containsKey(structureName)) return false;
        BuildStructure structure = new BuildStructure(structureName,
                new BlockPosition(
                sender.getLocation().getBlockX(),
                sender.getLocation().getBlockY(),
                sender.getLocation().getBlockZ()));
        STRUCTURES.put(structureName, structure);
        return true;
    }
    
    public static void save(BuildStructure structure) {
        if (structure != null) {
            if (fieldCol.find(new Document("name", structure.getName())).first() != null) {
                fieldCol.deleteMany(new Document("name", structure.getName()));
            }
            fieldCol.insertOne(structure.save());
        }
    }
    
    public static int edit(CorePlayer player, String structureName) {
        BuildStructure structure = STRUCTURES.get(structureName);
        if (structure == null) {
            return 1;
        }
        if (structure.isUnderConstruction()) {
            return 2;
        }
        if (!player.isInGlobal()) {
            return 3;
        }
        BuildWorld.createBuildWorld(player, structure);
        return 0;
    }
    
    public static boolean destroy(String structureName) {
        BuildStructure field = STRUCTURES.get(structureName);
        if (field != null && !field.isUnderConstruction()) {
            if (fieldCol.find(new Document("name", structureName)).first() != null) {
                fieldCol.deleteMany(new Document("name", structureName));
            }
            STRUCTURES.remove(structureName);
            return true;
        }
        return false;
    }
    
    public static BuildStructure get(String fieldName) {
        return STRUCTURES.get(fieldName);
    }
    
    public static Set<String> getNames() {
        return STRUCTURES.keySet();
    }
    
}
