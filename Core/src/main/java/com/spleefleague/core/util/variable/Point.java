/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import com.comphenix.protocol.wrappers.BlockPosition;
import java.util.ArrayList;
import java.util.List;

import com.spleefleague.coreapi.database.variable.DBVariable;
import net.minecraft.server.v1_16_R1.Vec3D;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class Point extends DBVariable<List<Double>> {

    public double x, y, z;
    
    public Point() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Point(Vec3D vec) {
        this.x = vec.getX();
        this.y = vec.getY();
        this.z = vec.getZ();
    }
    public Point(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
    }
    public Point(Vector vec) {
        this.x = vec.getX();
        this.y = vec.getY();
        this.z = vec.getZ();
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
    
    public Vector toVector() {
        return new Vector(x, y, z);
    }
    
    public Point add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public List<RaycastResult> cast(Vector direction, Vector size, double maxDist, List<Entity> entities) {
        List<EntityRaycastResult> entityResults = castEntities(direction, size, maxDist, entities);
        List<BlockRaycastResult> blockResults = castBlocks(direction, maxDist);

        List<RaycastResult> results = new ArrayList<>(blockResults);

        int i = 0;
        for (EntityRaycastResult entityResult : entityResults) {
            while (i < results.size() && results.get(i).getDistance() < entityResult.getDistance()) {
                i++;
            }
            results.add(i, entityResult);
            i++;
        }

        return results;
    }

    public List<EntityRaycastResult> castEntities(Vector direction, Vector size, double maxDist, List<Entity> entities) {
        List<EntityRaycastResult> results = new ArrayList<>();
        if (maxDist <= 0.00001) return results;
        direction = direction.normalize();
        for (Entity entity : entities) {
            BoundingBox bb = entity.getBoundingBox().clone().expand(size);
            RayTraceResult rtr = bb.rayTrace(toVector(), direction, maxDist);
            if (rtr != null) {
                results.add(new EntityRaycastResult(rtr.getHitPosition().subtract(toVector()).length(), rtr.getHitPosition(), entity));
            }
        }
        results.sort((l, r) -> (int) (l.getDistance() * 1000 - r.getDistance() * 1000));
        return results;
    }
    
    public List<BlockRaycastResult> castBlocks(Vector direction, double maxDist) {
        direction = direction.normalize();
        double px = x < 0 ? x + -Math.floor(x) : x;
        double py = y < 0 ? y + -Math.floor(y) : y;
        double pz = z < 0 ? z + -Math.floor(z) : z;
        double distX = direction.getX() > 0 ? (1 - (px % 1)) : (px % 1);
        double distY = direction.getY() > 0 ? (1 - (py % 1)) : (py % 1);
        double distZ = direction.getZ() > 0 ? (1 - (pz % 1)) : (pz % 1);
        
        double requiredX = Math.abs(direction.getX()) <= 0.001D ? 1000 : 1D / Math.abs(direction.getX());
        double requiredY = Math.abs(direction.getY()) <= 0.001D ? 1000 : 1D / Math.abs(direction.getY());
        double requiredZ = Math.abs(direction.getZ()) <= 0.001D ? 1000 : 1D / Math.abs(direction.getZ());
        
        double remainX = distX * requiredX;
        double remainY = distY * requiredY;
        double remainZ = distZ * requiredZ;
        
        List<BlockRaycastResult> result = new ArrayList<>();
        
        BlockPosition cPos = new BlockPosition((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
        double cDist = 0;
        int axis = 0;
        BlockFace face = BlockFace.SELF;
        
        // Distance to faces for initial axis
        // Closer to 0 is closer to face
        double fx = 1.0 - (Math.abs(Math.abs(x) - 0.5) * 2);
        double fy = 1.0 - (Math.abs(Math.abs(y) - 0.5) * 2);
        double fz = 1.0 - (Math.abs(Math.abs(z) - 0.5) * 2);
        if (fx < fy) {
            if (fx < fz) {
                axis = 1;
            } else {
                axis = 3;
            }
        } else {
            if (fy < fz) {
                axis = 2;
            } else {
                axis = 3;
            }
        }
        
        double minRemain = 0;
        while(maxDist > 0) {
            BlockRaycastResult rr = new BlockRaycastResult(cPos, cDist, (new Vector(x, y, z).add(direction.clone().multiply(cDist))), axis, face);
            result.add(rr);
            if (remainX < remainY) {
                if (remainX < remainZ) {
                    // Smallest remainder is X
                    minRemain = remainX;
                    remainX += requiredX;
                    cPos = cPos.add(new BlockPosition(direction.getX() > 0 ? 1 : -1, 0, 0));
                    axis = 1;
                    if (direction.getX() > 0)
                        face = BlockFace.WEST;
                    else
                        face = BlockFace.EAST;
                } else {
                    // Smallest remainder is Z
                    minRemain = remainZ;
                    remainZ += requiredZ;
                    cPos = cPos.add(new BlockPosition(0, 0, direction.getZ() > 0 ? 1 : -1));
                    axis = 3;
                    if (direction.getZ() > 0)
                        face = BlockFace.NORTH;
                    else
                        face = BlockFace.SOUTH;
                }
            } else if (remainY < remainZ) {
                // Smallest remainder is Y
                minRemain = remainY;
                remainY += requiredY;
                cPos = cPos.add(new BlockPosition(0, direction.getY() > 0 ? 1 : -1, 0));
                axis = 2;
                if (direction.getY() > 0)
                    face = BlockFace.DOWN;
                else
                    face = BlockFace.UP;
            } else {
                // Smallest remainder is Z
                minRemain = remainZ;
                remainZ = requiredZ;
                cPos = cPos.add(new BlockPosition(0, 0, direction.getZ() > 0 ? 1 : -1));
                axis = 3;
                if (direction.getZ() > 0)
                    face = BlockFace.NORTH;
                else
                    face = BlockFace.SOUTH;
            }
            maxDist -= minRemain;
            cDist += minRemain;
            remainX -= minRemain;
            remainY -= minRemain;
            remainZ -= minRemain;
        }
        
        List<BlockRaycastResult> resultSorted = new ArrayList<>();
        
        boolean inserted = false;
        for (BlockRaycastResult rr1 : result) {
            for (int i = 0; i < resultSorted.size(); i++) {
                BlockRaycastResult rr2 = resultSorted.get(i);
                if (rr1.getDistance() < rr2.getDistance()) {
                    resultSorted.add(i, rr1);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                resultSorted.add(rr1);
            }
        }
        
        return resultSorted;
    }
    
    @Override
    public void load(List<Double> list) {
        x = list.get(0);
        y = list.get(1);
        z = list.get(2);
    }
    
    @Override
    public List<Double> save() {
        List<Double> list = new ArrayList<>();
        list.add(x);
        list.add(y);
        list.add(z);
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (Double.compare(point.x, x) != 0) return false;
        if (Double.compare(point.y, y) != 0) return false;
        return Double.compare(point.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
