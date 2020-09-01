/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import java.util.ArrayList;
import java.util.List;

import com.spleefleague.core.logger.CoreLogger;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Essentially a Location without the world variable
 *
 * @author NickM13
 */
public class Position extends DBVariable<List<?>> {
    
    /**
     * For database usage, requires list of 5 numbers
    */

    public double x, y, z;
    long yaw, pitch;
    
    public Position() { }
    
    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = this.pitch = 0;
    }
    
    public Position(double x, double y, double z, long yaw, long pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public Position(Location loc) {
        x = Math.round(loc.getX() * 4) / 4D;
        y = Math.round(loc.getY() * 4) / 4D;
        z = Math.round(loc.getZ() * 4) / 4D;
        yaw = Math.round(loc.getYaw() / 15) * 15;
        pitch = Math.round(loc.getPitch() / 15) * 15;
    }
    
    public Position(List<?> list) {
        super(list);
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getZ() {
        return z;
    }
    
    public Position add(double x, double y, double z) {
        return new Position(this.x + x, this.y + y, this.z + z, yaw, pitch);
    }
    
    public BlockPosition toBlockPosition() {
        return new BlockPosition((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }
    
    public double getYaw() {
        return yaw;
    }
    
    public double getPitch() {
        return pitch;
    }
    
    private double doublefy(List<?> list, int i) {
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
    
    public double distance(Position pos) {
        return Math.sqrt(Math.pow(x - pos.x, 2) + Math.pow(y - pos.y, 2) + Math.pow(z - pos.z, 2));
    }
    
    @Override
    public void load(List<?> doc) {
        if (doc.size() >= 3) {
            x = doublefy(doc, 0);
            y = doublefy(doc, 1);
            z = doublefy(doc, 2);
            yaw = (long) doublefy(doc, 3);
            pitch = (long) doublefy(doc, 4);
        } else {
            CoreLogger.logError(null, new IndexOutOfBoundsException("" + doc.size()));
        }
    }
    
    @Override
    public List<?> save() {
        List<Double> list = new ArrayList<>();
        
        list.add(x);
        list.add(y);
        list.add(z);
        list.add((double) yaw);
        list.add((double) pitch);
        
        return list;
    }
    
    public Location toLocation(World world) {
        return new Location(world, x, y, z, (float)yaw, (float)pitch);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", Y:" + yaw + ", P:" + pitch + ")";
    }
    
}
