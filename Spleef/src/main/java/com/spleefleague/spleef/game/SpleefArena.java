/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.ArenaMode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.spleef.Spleef;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Location;

/**
 * Spleef Arenas are defined areas read from the Database
 * used in Spleef Battles, containing a number of players,
 * and the field of snow
 *
 * @author NickM13
 */

public class SpleefArena extends Arena {
    
    protected SpleefField field;
    protected Point center = new Point();

    /**
     * Boxes that the snow is filled into
     * ObjectId links to the SuperSpleef:Fields document
     *
     * @param id ObjectID
     */
    @DBLoad(fieldName="field")
    protected void loadSpleefField(ObjectId id) {
        field = SpleefField.getField(id);
        
        Dimension bbdim = field.getAreas().get(0);
        for (int i = 1; i < field.getAreas().size(); i++) {
            Dimension dim = field.getAreas().get(i);
            bbdim.combine(dim);
        }
        center = bbdim.getCenter();
    }

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
    
    public SpleefField getField() {
        return field;
    }
    
}
