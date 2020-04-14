/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.io.converter;

import com.spleefleague.core.Core;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author NickM13
 */
public class LocationConverter {

    public static Location load(List list) {
        if (list == null || list.size() < 3) return null;
        
        double x = 0, y = 0, z = 0;
        float pitch = 0, yaw = 0;
        
        if (list.get(0) instanceof Integer) x = ((Integer) list.get(0)).doubleValue();
        else x = (double) list.get(0);
        if (list.get(1) instanceof Integer) y = ((Integer) list.get(1)).doubleValue();
        else y = (double) list.get(1);
        if (list.get(2) instanceof Integer) z = ((Integer) list.get(2)).doubleValue();
        else z = (double) list.get(2);
        
        if (list.size() >= 5) {
            if (list.get(3) instanceof Integer) pitch = ((Integer) list.get(3)).floatValue();
            else pitch = ((Double) list.get(3)).floatValue();
            if (list.get(4) instanceof Integer) yaw = ((Integer) list.get(4)).floatValue();
            else yaw = ((Double) list.get(4)).floatValue();
        }
        
        // Read world from list if size is divisible by 2 (4, 6, etc)
        World world = (list.size() % 2 == 0) ? Bukkit.getWorld((String) list.get(list.size() - 1)) : Core.DEFAULT_WORLD;
        return list.size() < 5 ? new Location(world, x, y, z) : new Location(world, x, y, z, pitch, yaw);
    }

    public static List save(Location v) {
        if (v == null) return null;
        List list = new ArrayList();
        list.add(v.getX());
        list.add(v.getY());
        list.add(v.getZ());
        if(v.getYaw() != 0.0 || v.getPitch() != 0.0) {
            list.add(v.getYaw());
            list.add(v.getPitch());
        }
        if (v.getWorld() != null && v.getWorld() != Core.DEFAULT_WORLD) {
            list.add(v.getWorld().getName());
        }
        return list;
    }
    
}
