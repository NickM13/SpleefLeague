/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.Spleef;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Spleef Arenas are defined areas read from the Database
 * used in Spleef Battles, containing a number of players,
 * and the field of snow
 *
 * @author NickM13
 */

public class SpleefArena extends Arena {
    
    @DBField
    protected List<String> structures;
    protected Point center = new Point();

    /**
     * Gets center of snow field (Used for facing players towards center)
     *
     * @return Center Point
     */
    public Point getCenter() {
        return new Point(center.x, center.y, center.z);
    }

    /**
     * Initialize all Spleef arenas from the SuperSpleef:Arenas database
     * All Spleef arenas also attempt to load as Splegg arenas temporarily
     * TODO: Move Splegg to its own *plugin?*
     *
     */
    public static void init() {
        Iterator<Document> arenaTypes = Spleef.getInstance().getPluginDB().getCollection("Arenas").aggregate(Arrays.asList(
                new Document("$unwind", new Document("path", "$spleefMode")),
                new Document("$group", new Document("_id", "$spleefMode").append("arenas", new Document("$addToSet", "$$ROOT")))
        )).iterator();
        while (arenaTypes.hasNext()) {
            Document arenas = arenaTypes.next();
            List<Document> arenaInstances = arenas.get("arenas", List.class);
            Iterator<Document> arenaIt = arenaInstances.iterator();
            try {
                ArenaMode mode = SpleefMode.valueOf(arenas.get("_id", String.class)).getArenaMode();
                int amount = 0;
                amount = loadArenas(arenaIt, mode);
                if (amount > 0) System.out.println("Loaded " + amount + " " + mode.getDisplayName() + " arenas.");
            } catch(IllegalArgumentException e) {
                System.err.println(arenas.get("_id") + " is not a valid spleef mode.");
            }
        }
    }
    
    public List<BuildStructure> getFields() {
        List<BuildStructure> buildStructures = new ArrayList<BuildStructure>();
        for (String structure : structures) {
            buildStructures.add(BuildStructures.get(structure));
        }
        return buildStructures;
    }
    
}
