package com.spleefleague.core.world.game;

import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import com.spleefleague.core.world.game.projectile.ProjectileWorldPlayer;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class GameUtils {

    public static void spawnPlayerParticles(BattlePlayer bp, Particle.DustOptions dustOptions, double sizeMultiplier) {
        bp.getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                bp.getPlayer().getLocation().getX(),
                bp.getPlayer().getLocation().getY() + 0.9,
                bp.getPlayer().getLocation().getZ(),
                8, 0.25 * sizeMultiplier, 0.9 * sizeMultiplier, 0.25 * sizeMultiplier, 0D, dustOptions);
    }

    public static void spawnCircleParticles(ProjectileWorld<? extends ProjectileWorldPlayer> gameWorld,
                                            Vector loc,
                                            Vector axis,
                                            Particle.DustOptions dustOptions,
                                            double radius,
                                            double rotationRadians,
                                            double rotationSection,
                                            int count) {
        double dot = axis.dot(new Vector(0, 1, 0));
        if (dot >= 0.999 || dot <= -0.999) {
            spawnCircleParticles(gameWorld, loc, dustOptions, radius, rotationRadians, rotationSection, count);
            return;
        }
        Vector rotAxis = axis.getCrossProduct(new Vector(0, 1, 0));
        double angle = Math.toRadians(90 * (-dot + 1));
        for (int i = 0; i < count; i++) {
            double radians = ((double) i / count) * rotationSection + rotationRadians;
            Vector vec = new Vector(Math.sin(radians), 0, Math.cos(radians)).rotateAroundAxis(rotAxis, angle).multiply(radius);
            vec.add(loc);
            gameWorld.spawnParticles(Particle.REDSTONE,
                    vec.getX(), vec.getY(), vec.getZ(),
                    1, 0, 0, 0, 0,
                    dustOptions);
        }
    }

    public static void spawnCircleParticles(ProjectileWorld<? extends ProjectileWorldPlayer> gameWorld,
                                            Vector loc,
                                            Particle.DustOptions dustOptions,
                                            double radius,
                                            double rotationRadians,
                                            double rotationSection,
                                            int count) {
        for (int i = 0; i < count; i++) {
            double radians = (i / (double) count) * rotationSection + rotationRadians;
            Vector pos = loc.clone().add(new Vector(Math.sin(radians), 0, Math.cos(radians)).multiply(radius));
            gameWorld.spawnParticles(Particle.REDSTONE,
                    pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 0, 0, 0,
                    dustOptions);
        }
    }

    public static void spawnRingParticles(ProjectileWorld<? extends ProjectileWorldPlayer> gameWorld,
                                          Vector loc,
                                          Vector axis,
                                          Particle.DustOptions dustOptions,
                                          double radius,
                                          int count) {
        double dot = axis.dot(new Vector(0, 1, 0));
        if (dot >= 0.999 || dot <= -0.999) {
            spawnRingParticles(gameWorld, loc, dustOptions, radius, count);
            return;
        }
        Vector rotAxis = axis.getCrossProduct(new Vector(0, 1, 0));
        double angle = Math.toRadians(90 * (-dot + 1));
        for (int i = 0; i < count; i++) {
            double radians = Math.random() * Math.PI * 2;
            Vector vec = new Vector(Math.sin(radians), 0, Math.cos(radians)).rotateAroundAxis(rotAxis, angle).multiply(radius);
            vec.add(loc);
            gameWorld.spawnParticles(Particle.REDSTONE,
                    vec.getX(), vec.getY(), vec.getZ(),
                    1, 0, 0, 0, 0,
                    dustOptions);
        }
    }

    public static void spawnRingParticles(ProjectileWorld<? extends ProjectileWorldPlayer> gameWorld, Vector loc, Particle.DustOptions dustOptions, double radius, int count) {
        for (int i = 0; i < count; i++) {
            double radians = Math.random() * Math.PI * 2;
            Vector pos = loc.clone().add(new Vector(Math.sin(radians), 0, Math.cos(radians)).multiply(radius));
            gameWorld.spawnParticles(Particle.REDSTONE,
                    pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 0, 0, 0,
                    dustOptions);
        }
    }

    public static void spawnDiscParticles(ProjectileWorld<? extends ProjectileWorldPlayer> gameWorld, Vector loc, Vector axis, Particle.DustOptions dustOptions, double radius, int count) {
        double dot = axis.dot(new Vector(0, 1, 0));
        if (dot >= 0.999 || dot <= -0.999) {
            spawnDiscParticles(gameWorld, loc, dustOptions, radius, count);
            return;
        }
        Vector rotAxis = axis.getCrossProduct(new Vector(0, 1, 0));
        double angle = Math.toRadians(90 * (-dot + 1));
        for (int i = 0; i < count; i++) {
            double radians = Math.random() * Math.PI * 2;
            double dist = (1 - Math.pow(Math.random(), 2)) * radius;
            Vector vec = new Vector(Math.sin(radians), 0, Math.cos(radians)).rotateAroundAxis(rotAxis, angle).multiply(dist);
            vec.add(loc);
            gameWorld.spawnParticles(Particle.REDSTONE,
                    vec.getX(), vec.getY(), vec.getZ(),
                    1, 0, 0, 0, 0,
                    dustOptions);
        }
    }

    public static void spawnDiscParticles(ProjectileWorld<? extends ProjectileWorldPlayer> gameWorld, Vector loc, Particle.DustOptions dustOptions, double radius, int count) {
        for (int i = 0; i < count; i++) {
            double radians = Math.random() * Math.PI * 2;
            double dist = (1 - Math.pow(Math.random(), 2)) * radius;
            Vector pos = loc.clone().add(new Vector(Math.sin(radians), 0, Math.cos(radians)).multiply(dist));
            gameWorld.spawnParticles(Particle.REDSTONE,
                    pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 0, 0, 0,
                    dustOptions);
        }
    }

    public static void spawnParticles(ProjectileWorld<? extends ProjectileWorldPlayer> gameWorld, Vector loc, Particle.DustOptions dustOptions, int count, double spread) {
        gameWorld.spawnParticles(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), count, spread, spread, spread, 0, dustOptions);
    }

    public static void spawnCubeParticles() {

    }

}
