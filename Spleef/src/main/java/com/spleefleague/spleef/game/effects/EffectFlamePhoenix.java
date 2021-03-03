package com.spleefleague.spleef.game.effects;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.global.GlobalWorld;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 3/1/2021
 */
public class EffectFlamePhoenix {

    private static final String[][] PHOENIX_TEXT = new String[][]{{
            "000000020000000",
            "000000020000000",
            "211000010000112",
            "000100010001000",
            "000011111110000"
    },{
            "000000020000000",
            "000000020000000",
            "000000010000000",
            "211111111111112"
    },{
            "000000020000000",
            "000000020000000",
            "211000010000112",
            "000100010001000",
            "000011111110000"
    },{
            "000200020002000",
            "000100020001000",
            "000100010001000",
            "000100010001000",
            "000011111110000"
    }};

    private static final List<List<Vector>> PHOENIX_DUST = new ArrayList<>();
    private static final List<List<Vector>> PHOENIX_FIRE = new ArrayList<>();

    static {
        double OFFSET = 0.4;
        for (String[] frame : PHOENIX_TEXT) {
            double vpos = (PHOENIX_TEXT.length / 2D) * OFFSET;
            List<Vector> dust = new ArrayList<>();
            List<Vector> fire = new ArrayList<>();
            PHOENIX_DUST.add(dust);
            PHOENIX_FIRE.add(fire);
            for (String line : frame) {
                double hpos = (line.length() / 2D) * -OFFSET;
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    switch (c) {
                        case '1':
                            dust.add(new Vector(hpos, vpos, 0));
                            break;
                        case '2':
                            fire.add(new Vector(hpos, vpos, 0));
                            break;
                    }
                    hpos += OFFSET;
                }
                vpos -= OFFSET;
            }
        }
    }

    private final Vector center;
    private final Vector velocity;

    private boolean alive = true;

    private final GlobalWorld gameWorld;

    private int frameCounter = 0;

    private final List<List<Vector>> dustFrames = new ArrayList<>();
    private final List<List<Vector>> fireFrames = new ArrayList<>();

    public EffectFlamePhoenix(GlobalWorld globalWorld, Location location, double speed) {
        this.gameWorld = globalWorld;
        this.center = location.toVector();
        Vector direction = location.getDirection();
        this.velocity = direction.clone().multiply(speed);
        float pitch = location.getPitch();
        float yaw = location.getYaw();
        for (List<Vector> frame : PHOENIX_DUST) {
            List<Vector> rotatedFrame = new ArrayList<>();
            for (Vector pixel : frame) {
                rotatedFrame.add(pixel.clone().rotateAroundX(Math.toRadians(pitch)).rotateAroundY(Math.toRadians(-yaw)));
            }
            dustFrames.add(rotatedFrame);
        }
        for (List<Vector> frame : PHOENIX_FIRE) {
            List<Vector> rotatedFrame = new ArrayList<>();
            for (Vector pixel : frame) {
                rotatedFrame.add(pixel.clone().rotateAroundX(Math.toRadians(pitch)).rotateAroundY(Math.toRadians(-yaw)));
            }
            fireFrames.add(rotatedFrame);
        }
    }

    public Vector getCenter() {
        return center;
    }

    private static final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 0.85f);

    private static final int tickPerFrame = 6;

    public void forward() {
        if (!alive) return;
        BlockPosition pos = new BlockPosition(center.getBlockX(), center.getBlockY(), center.getBlockZ());
        if (!gameWorld.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).isPassable()) {
            alive = false;
            return;
        }
        for (Vector pixel : dustFrames.get(frameCounter / tickPerFrame)) {
            gameWorld.spawnParticles(Particle.REDSTONE,
                    pixel.getX() + center.getX(),
                    pixel.getY() + center.getY(),
                    pixel.getZ() + center.getZ(),
                    1, 0, 0, 0, 0,
                    dustOptions);
        }
        for (Vector pixel : fireFrames.get(frameCounter / tickPerFrame)) {
            gameWorld.spawnParticles(Particle.FLAME,
                    pixel.getX() + center.getX(),
                    pixel.getY() + center.getY(),
                    pixel.getZ() + center.getZ(),
                    1, 0, 0, 0, 0);
        }
        center.add(velocity);
        frameCounter++;
        if (frameCounter / tickPerFrame >= PHOENIX_DUST.size()) {
            frameCounter = 0;
        }
    }

    public boolean isAlive() {
        return alive;
    }

}
