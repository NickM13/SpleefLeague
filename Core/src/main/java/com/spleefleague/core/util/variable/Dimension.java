/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import com.spleefleague.core.io.converter.LocationConverter;
import com.spleefleague.core.database.variable.DBVariable;
import java.util.List;
import org.bson.Document;
import org.bukkit.Location;

/**
 * @author NickM13
 */
public class Dimension extends DBVariable<Document> {
    private Point low, high;
    
    public Dimension() {
        low = new Point();
        high = new Point();
    }
    public Dimension(Dimension dim) {
        low = new Point(dim.low.x, dim.low.y, dim.low.z);
        high = new Point(dim.high.x, dim.high.y, dim.high.z);
    }
    public Dimension(Point p1, Point p2) {
        low = new Point();
        high = new Point();
        equalize(p1, p2);
    }
    
    /**
     * Sets the lower and higher values based on two points
     *
     * @param p1 Point 1
     * @param p2 Point 2
     */
    private void equalize(Point p1, Point p2) {
        if (p1.x < p2.x) {
            low.x = p1.x;
            high.x = p2.x;
        } else {
            low.x = p2.x;
            high.x = p1.x;
        }
        
        if (p1.y < p2.y) {
            low.y = p1.y;
            high.y = p2.y;
        } else {
            low.y = p2.y;
            high.y = p1.y;
        }
        
        if (p1.z < p2.z) {
            low.z = p1.z;
            high.z = p2.z;
        } else {
            low.z = p2.z;
            high.z = p1.z;
        }
    }
    
    /**
     * Lower point of dimension
     *
     * @return Lower Point
     */
    public Point getLow() {
        return low;
    }
    
    /**
     * Higher point of dimension
     *
     * @return Higher Point
     */
    public Point getHigh() {
        return high;
    }
    
    public Point getCenter() {
        return new Point(
                low.x + (high.x - low.x) / 2,
                low.y + (high.y - low.y) / 2,
                low.z + (high.z - low.z) / 2);
    }
    
    /**
     * Checks if a point is contained within lower and higher points (inclusive)
     *
     * @param p Point
     * @return Contained
     */
    public boolean isContained(Point p) {
        return (p.x >= low.x && p.x <= high.x &&
                p.y >= low.y && p.y <= high.y &&
                p.z >= low.z && p.z <= high.z);
    }
    
    /**
     * Returns a new dimension with expanded boundaries
     *
     * @param x X increase
     * @param y Y increase
     * @param z Z increase
     * @return Expanded Dimension
     */
    public Dimension expand(double x, double y, double z) {
        Dimension dim = new Dimension(this);
        dim.low.x -= x;
        dim.low.y -= y;
        dim.low.z -= z;
        
        dim.high.x += x;
        dim.high.y += y;
        dim.high.z += z;
        return dim;
    }
    
    /**
     * Returns a new dimension with expanded boundaries
     *
     * @param v Increase Value
     * @return Expanded Dimension
     */
    public Dimension expand(double v) {
        return expand(v, v, v);
    }
    
    /**
     * Merges a dimension into this one, using the lowest and highest values from both
     *
     * @param dim Dimension to Merge
     */
    public void combine(Dimension dim) {
        low.x = Math.min(low.x, dim.low.x);
        low.y = Math.min(low.y, dim.low.y);
        low.z = Math.min(low.z, dim.low.z);
        
        high.x = Math.min(high.x, dim.high.x);
        high.y = Math.min(high.y, dim.high.y);
        high.z = Math.min(high.z, dim.high.z);
    }

    @Override
    public void load(Document doc) {
        Point p1 = new Point();
        p1.load(doc.get("low", List.class));
        Point p2 = new Point();
        p2.load(doc.get("high", List.class));
        equalize(p1, p2);
    }

    @Override
    public Document save() {
        return new Document("low", low.save()).append("high", high.save());
    }
    
    @Override
    public String toString() {
        return "(" + low + ", " + high + ")";
    }
}
