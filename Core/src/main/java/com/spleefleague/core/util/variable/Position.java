/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import com.spleefleague.core.database.variable.DBVariable;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Essentially a Location without the world variable
 *
 * @author NickM13
 */
public class Position extends DBVariable<List> {
    
    /**
     * For database usage, requires list of 5 numbers
    */

    public double x, y, z, pitch, yaw;
    
    public Position() {
    
    }
    
    public Position(Location loc) {
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        pitch = loc.getPitch();
        yaw = loc.getYaw();
    }
    
    private double doublefy(List list, int i) {
        double num = -1;
        if (list != null && i < list.size()) {
            Object o = list.get(i);
            if (o instanceof Double) {
                num = ((Double)o);
            } else if (o instanceof Float) {
                num = ((Float)o).doubleValue();
            } else if (o instanceof Integer) {
                num = ((Integer)o).doubleValue();
            } else if (o instanceof Long) {
                num = ((Long)o).doubleValue();
            } else if (o instanceof Short) {
                num = ((Short)o).doubleValue();
            }
        }
        return num;
    }
    
    @Override
    public void load(List doc) {
        if (doc.size() >= 3) {
            x = doublefy(doc, 0);
            y = doublefy(doc, 1);
            z = doublefy(doc, 2);
            pitch = doublefy(doc, 3);
            yaw = doublefy(doc, 4);
        } else {
            System.out.println("Error loading position variable");
            Thread.dumpStack();
        }
    }
    
    @Override
    public List save() {
        List list = new ArrayList<>();
        
        list.add(x);
        list.add(y);
        list.add(z);
        list.add(pitch);
        list.add(yaw);
        
        return list;
    }
    
    public Location asLocation(World world) {
        return new Location(world, x, y, z, (float)pitch, (float)yaw);
    }
    
}
