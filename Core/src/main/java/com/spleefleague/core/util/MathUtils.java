package com.spleefleague.core.util;

import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import com.spleefleague.core.world.game.projectile.ProjectileWorldPlayer;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class MathUtils {

    public static double cos(double radians, int decimals) {
        return Math.round(Math.cos(radians) * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    public static double sin(double radians, int decimals) {
        return Math.round(Math.sin(radians) * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    public static Vector getPointOnCircleHorizontal(Vector loc,
                                                  double radius,
                                                  double radians) {
        return loc.clone().add(new Vector(Math.sin(radians), 0, Math.cos(radians)).multiply(radius));
    }

    public static Vector getPointOnCircle(Vector loc,
                                          Vector axis,
                                          double radius,
                                          double radians) {
        double dot = axis.dot(new Vector(0, 1, 0));
        if (dot >= 0.999 || dot <= -0.999) {
            return getPointOnCircleHorizontal(loc, radius, radians);
        }
        Vector rotAxis = axis.getCrossProduct(new Vector(0, 1, 0));
        double angle = Math.toRadians(90 * (-dot + 1));
        return new Vector(Math.sin(radians), 0, Math.cos(radians)).rotateAroundAxis(rotAxis, angle).multiply(radius).add(loc);
    }

}
