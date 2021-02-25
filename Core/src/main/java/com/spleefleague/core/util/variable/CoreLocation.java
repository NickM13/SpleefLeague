/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Essentially a Location without the world variable
 *
 * @author NickM13
 */
public class CoreLocation extends DBVariable<List<?>> {

    /**
     * For database usage, requires list of 5 numbers
     */

    public double x;
    public double y;
    public double z;
    public long yaw;
    public long pitch;
    public String worldName;

    public CoreLocation() {
    }

    public CoreLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = this.pitch = 0;
        this.worldName = Core.DEFAULT_WORLD.getName();
    }

    public CoreLocation(double x, double y, double z, long yaw, long pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = Core.DEFAULT_WORLD.getName();
    }

    public CoreLocation(Location loc) {
        x = Math.round(loc.getX() * 4) / 4D;
        y = Math.round(loc.getY() * 4) / 4D;
        z = Math.round(loc.getZ() * 4) / 4D;
        yaw = Math.round(loc.getYaw() / 15) * 15L;
        pitch = Math.round(loc.getPitch() / 15) * 15L;
        worldName = Objects.requireNonNull(loc.getWorld()).getName();
    }

    public CoreLocation(Location loc, double roundDivisor) {
        x = Math.round(loc.getX() * roundDivisor) / roundDivisor;
        y = Math.round(loc.getY() * roundDivisor) / roundDivisor;
        z = Math.round(loc.getZ() * roundDivisor) / roundDivisor;
        yaw = (long) (Math.round(loc.getYaw() / (90 / roundDivisor)) * (90 / roundDivisor));
        pitch = (long) (Math.round(loc.getPitch() / (90 / roundDivisor)) * (90 / roundDivisor));
        worldName = Objects.requireNonNull(loc.getWorld()).getName();
    }

    public CoreLocation(List<?> list) {
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

    public CoreLocation add(double x, double y, double z) {
        return new CoreLocation(this.x + x, this.y + y, this.z + z, yaw, pitch);
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
                num = ((Double) o);
            } else if (o instanceof Float) {
                num = ((Float) o).doubleValue();
            } else if (o instanceof Integer) {
                num = ((Integer) o).doubleValue();
            } else if (o instanceof Long) {
                num = ((Long) o).doubleValue();
            } else if (o instanceof Short) {
                num = ((Short) o).doubleValue();
            }
        }
        return num;
    }

    public double distance(CoreLocation pos) {
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
            if (doc.size() > 5) {
                worldName = (String) doc.get(5);
            } else {
                worldName = Core.DEFAULT_WORLD.getName();
            }
        } else {
            CoreLogger.logError(null, new IndexOutOfBoundsException("" + doc.size()));
        }
    }

    @Override
    public List<?> save() {
        List<Object> list = new ArrayList<>();

        list.add(x);
        list.add(y);
        list.add(z);
        list.add((double) yaw);
        list.add((double) pitch);
        list.add(worldName);

        return list;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, (float) yaw, (float) pitch);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", Y:" + yaw + ", P:" + pitch + ")";
    }

}
