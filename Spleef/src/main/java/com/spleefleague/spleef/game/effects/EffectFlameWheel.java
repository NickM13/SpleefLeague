package com.spleefleague.spleef.game.effects;

import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 3/1/2021
 */
public class EffectFlameWheel {

    private final Vector center;
    private final Vector direction;
    private final Vector velocity;
    private final Vector up;
    private final Vector right;

    private boolean alive = true;

    private final World world;

    private final double radius;

    public EffectFlameWheel(World world, Location location, double radius, double speed) {
        this.world = world;
        this.center = location.toVector();
        this.direction = location.getDirection();
        this.velocity = direction.clone().multiply(speed);
        this.up = direction.rotateAroundZ(Math.toRadians(90));
        Location loc = location.clone();
        loc.setPitch(0);
        loc.setYaw(loc.getYaw() + 90);
        this.right = loc.getDirection();
        this.radius = radius;
    }

    public Vector getCenter() {
        return center;
    }

    public boolean forward() {
        BlockPosition pos = new BlockPosition(center.getBlockX(), center.getBlockY(), center.getBlockZ());
        if (!world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()).isPassable()) {
            alive = false;
            return false;
        }
        center.add(velocity);
        return true;
    }

    public boolean isAlive() {
        return alive;
    }

    public Vector getRight() {
        return right;
    }

}
