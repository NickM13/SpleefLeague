package com.spleefleague.spleef.game.effects;

import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.spleef.game.ShovelEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author NickM13
 * @since 3/1/2021
 */
public class EffectLightning {

    private static final int DEVIATION = 10;
    private static final double NODE_DISTANCE = 0.3;
    private static final int SPLIT_DEVIATION = 45;

    private final GlobalWorld globalWorld;
    private final List<Vector> nodes = new ArrayList<>();
    private final Location location;

    public EffectLightning(GlobalWorld globalWorld, Location location, Vector direction, int nodeCount) {
        createBranch(location.toVector(), direction.clone(), nodeCount);
        this.globalWorld = globalWorld;
        this.location = location;
    }

    private void createBranch(Vector position, Vector direction, int nodeCount) {
        Random random = new Random();
        nodes.add(position.clone());
        for (int i = nodeCount; i >= 0; i--) {
            Vector axis = new Vector(1, 0, 0).rotateAroundZ(random.nextDouble() * Math.PI);
            if (i >= 3 && Math.random() < 0.05) {
                double split = Math.toRadians(random.nextInt(SPLIT_DEVIATION * 2) - SPLIT_DEVIATION);
                createBranch(position.clone(), direction.clone().rotateAroundAxis(axis, split * 1.75), i - 3);
                direction = direction.rotateAroundAxis(axis, split * 0.25);
                i--;
            } else {
                direction = direction.rotateAroundAxis(axis, Math.toRadians(random.nextInt(DEVIATION * 2) - DEVIATION));
            }
            position.add(direction.clone().multiply(NODE_DISTANCE));
            nodes.add(position.clone());
        }
    }

    public void play() {
        globalWorld.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 2, "Sound:Shovel");
        for (Vector node : nodes) {
            globalWorld.spawnParticles(Particle.REDSTONE,
                    node.getX(), node.getY(), node.getZ(),
                    1, 0, 0, 0, 0,
                    ShovelEffect.ShovelEffectType.LIGHTNING.dustOptions);

            /*
            globalWorld.spawnParticles(Particle.BUBBLE_POP,
                    node.getX(), node.getY(), node.getZ(),
                    1, 0, 0, 0, 0.1);
            */
        }
    }

}
