/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.util.Dimension;
import com.spleefleague.core.util.Point;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Location;

/**
 * @author NickM13
 */

public class SpleefArena extends Arena {
    /*
    Spleef Arenas are the defined areas that a Spleef Battle
    will take place in, containing the number of players, 
    field size, world, and other variables
    */
    
    protected SpleefField field;
    protected Point center = new Point();
    
    @DBLoad(fieldname="field")
    protected void loadSpleefField(ObjectId id) {
        field = SpleefField.getField(id);
        
        Dimension bbdim = field.getAreas().get(0);
        for (int i = 1; i < field.getAreas().size(); i++) {
            Dimension dim = field.getAreas().get(i);
            bbdim.combine(dim);
        }
        center = bbdim.getCenter();
    }
    
    public Point getCenter() {
        return new Point(center.x, center.y, center.z);
    }
    
    public static void init() {
        Iterator<Document> arenaTypes = Core.getInstance().getMongoClient().getDatabase("SuperSpleef").getCollection("Arenas").aggregate(Arrays.asList(
                new Document("$unwind", new Document("path", "$spleefMode")),
                new Document("$group", new Document("_id", "$spleefMode").append("arenas", new Document("$addToSet", "$$ROOT")))
        )).iterator();
        while(arenaTypes.hasNext()) {
            Document arenas = arenaTypes.next();
            List<Document> arenaInstances = arenas.get("arenas", List.class);
            try {
                ArenaMode mode = SpleefMode.valueOf(arenas.get("_id", String.class)).getArenaMode();
                int amount = 0;
                amount = loadArenas(arenaInstances, mode);
                if (amount > 0) System.out.println("Loaded " + amount + " " + mode.getDisplayName() + " arenas.");
            } catch(IllegalArgumentException e) {
                System.err.println(arenas.get("_id") + " is not a valid spleef mode.");
            }
            
            try {
                ArenaMode mode = SpleggMode.valueOf(arenas.get("_id", String.class)).getArenaMode();
                int amount = 0;
                amount = loadArenas(arenaInstances, mode);
                if (amount > 0) System.out.println("Loaded " + amount + " " + mode.getDisplayName() + " arenas.");
            } catch(IllegalArgumentException e) {
                System.err.println(arenas.get("_id") + " is not a valid splegg mode.");
            }
        }
    }
    
    public SpleefField getField() {
        return field;
    }
    
}
