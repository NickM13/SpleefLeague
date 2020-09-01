package com.spleefleague.core.world.game;

import com.spleefleague.core.game.battle.BattlePlayer;
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

    public static void spawnRingParticles(GameWorld gameWorld, Vector loc, Particle.DustOptions dustOptions, double radius, int count) {
        for (int i = 0; i < count; i++) {
            double radians = Math.random() * Math.PI * 2;
            Vector pos = loc.clone().add(new Vector(Math.sin(radians), 0, Math.cos(radians)).multiply(radius));
            gameWorld.spawnParticles(Particle.REDSTONE,
                    pos.getX(), pos.getY(), pos.getZ(),
                    1, 0, 0, 0, 0,
                    dustOptions);
        }
    }

    public static void spawnParticles(GameWorld gameWorld, Vector loc, Particle.DustOptions dustOptions, int count, double spread) {
        gameWorld.spawnParticles(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), count, spread, spread, spread, 0, dustOptions);
    }

    public static void spawnCubeParticles() {

    }

}
