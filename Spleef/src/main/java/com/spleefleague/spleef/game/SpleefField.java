/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.variable.DBEntity;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.spleef.Spleef;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class SpleefField extends DBEntity {
    
    private static final Map<ObjectId, SpleefField> FIELDS = new HashMap<>();
    
    // Array of areas to fill for the field
    private final List<Dimension> areas = new ArrayList<>();
    
    @DBLoad(fieldName="field")
    public void setAreas(List<Document> areadoc) {
        for (Document d : areadoc) {
            Dimension dim = new Dimension();
            dim.load(d);
            areas.add(dim);
        }
        fillGlobalWorld();
    }
    
    public void fillGlobalWorld() {
        for (Dimension area : areas) {
            for (int x = (int) area.getLow().x; x <= area.getHigh().x; x++) {
                for (int y = (int) area.getLow().y; y <= area.getHigh().y; y++) {
                    for (int z = (int) area.getLow().z; z <= area.getHigh().z; z++) {
                        FakeWorld.getGlobalFakeWorld().setBlock(new BlockPosition(x, y, z), Material.SNOW_BLOCK.createBlockData());
                    }
                }
            }
        }
    }

    public List<Dimension> getAreas() {
        return areas;
    }
    
    public static SpleefField getField(ObjectId id) {
        return FIELDS.get(id);
    }
    
    public static void init() {
        MongoCursor<Document> cursor = Spleef.getInstance().getPluginDB().getCollection("Fields").find().iterator();
        while (cursor.hasNext()) {
            Document d = cursor.next();
            try {
                SpleefField field = new SpleefField();
                field.load(d);
                FIELDS.put(field.getId(), field);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
