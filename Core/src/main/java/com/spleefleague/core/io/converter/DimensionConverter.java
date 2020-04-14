/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.io.converter;

import com.spleefleague.core.util.Dimension;
import com.spleefleague.core.util.Point;
import java.util.List;
import org.bson.Document;
import org.bukkit.Location;

/**
 * @author NickM13
 */
public class DimensionConverter {

    public static Dimension load(Document doc) {
        Location loc1 = LocationConverter.load(doc.get("low", List.class));
        Location loc2 = LocationConverter.load(doc.get("high", List.class));
        
        Point p1 = new Point(loc1.getX(), loc1.getY(), loc1.getZ());
        Point p2 = new Point(loc2.getX(), loc2.getY(), loc2.getZ());
        
        return new Dimension(p1, p2);
    }

    public static Object toObject(Dimension t) {
        // TODO
        return new Document();
    }
}
