/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.mongodb.client.MongoCursor;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.util.Dimension;
import com.spleefleague.core.util.database.DBEntity;
import com.spleefleague.spleef.Spleef;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * @author NickM13
 */
public class SpleefField extends DBEntity {
    
    private static final Map<ObjectId, SpleefField> FIELDS = new HashMap<>();
    
    // Array of areas to fill for the field
    private final List<Dimension> areas = new ArrayList<>();
    
    @DBLoad(fieldname="field")
    public void setAreas(List<Document> areadoc) {
        for (Document d : areadoc) {
            Dimension dim = new Dimension();
            dim.load(d);
            areas.add(dim);
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
