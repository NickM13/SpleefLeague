package com.spleefleague.core.world.game.projectile;

import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.game.GameWorld;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.EntityThrownTrident;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.MovingObjectPosition;
import net.minecraft.server.v1_15_R1.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class FakeEntityTrident extends EntityThrownTrident {

    private final GameWorld gameWorld;
    private final ProjectileStats projectileStats;
    private Point lastLoc = null;
    private int bounces;

    public FakeEntityTrident(GameWorld gameWorld, CoreOfflinePlayer shooter, ProjectileStats projectileStats) {
        super(EntityTypes.TRIDENT, ((CraftWorld) gameWorld.getWorld()).getHandle());

        this.gameWorld = gameWorld;
        this.projectileStats = projectileStats;

        Location handLocation = shooter.getPlayer().getEyeLocation().clone()
                .add(shooter.getPlayer().getLocation().getDirection()
                        .crossProduct(new Vector(0, 1, 0)).normalize()
                        .multiply(0.15).add(new Vector(0, -0.15, 0)));
        setPositionRotation(handLocation.getX(), handLocation.getY(), handLocation.getZ(), handLocation.getPitch(), handLocation.getYaw());

        Random rand = new Random();
        Location lookLoc = shooter.getPlayer().getLocation().clone();
        if (projectileStats.hSpread > 0) {
            lookLoc.setYaw(lookLoc.getYaw() + rand.nextInt(projectileStats.hSpread) - (projectileStats.hSpread / 2.f));
        }
        if (projectileStats.vSpread > 0) {
            lookLoc.setPitch(lookLoc.getPitch() + rand.nextInt(projectileStats.vSpread) - (projectileStats.vSpread / 2.f));
        }
        Vector direction = lookLoc.getDirection().normalize().multiply(projectileStats.fireRange * 0.25);
        setMot(new Vec3D(direction.getX(), direction.getY(), direction.getZ()));

        setNoGravity(!projectileStats.gravity);
        this.bounces = projectileStats.bounces;
        this.noclip = projectileStats.noClip;
        this.inGround = true;
    }

    @Override
    public void tick() {
        super.tick();
        lastLoc = new Point(getPositionVector());
    }

    @Override
    protected void a(MovingObjectPosition var0) {
        if (!noclip) {
            super.a(var0);
        }
    }

    @Override
    public void pickup(EntityHuman entityhuman) {

    }

}
