package com.spleefleague.core.player.collectible.pet.pathfinder;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.pet.EntityPet;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;

/**
 * @author NickM13
 * @since 4/22/2020
 */
public class PathfinderGoalPetFetch extends PathfinderGoal {

    private double speed;
    private EntityPet entity;
    private NavigationAbstract navigation;
    private float minFollow;

    public PathfinderGoalPetFetch(EntityPet entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
    }

    private double ownerDist() {
        Location loc = entity.getOwner().getPlayer().getLocation();
        return entity.getPositionVector().distanceSquared(new Vec3D(loc.getX(), loc.getY(), loc.getZ()));
    }

    @Override
    public boolean a() {
        return entity.getOwner() != null && ownerDist() > (minFollow * minFollow);
    }

    @Override
    public boolean b() {
        if (entity.getOwner() != null) {
            return ownerDist() < (minFollow * minFollow);
        }
        return false;
    }

    @Override
    public void c() {
        if (entity.getOwner() != null) {
            Location loc = entity.getOwner().getPlayer().getLocation();
            PathEntity entityPath = navigation.a(new BlockPosition(loc.getX(), loc.getY(), loc.getZ()), 2);
            navigation.a(entityPath, speed);
        }
    }

    @Override
    public void d() {

    }

    @Override
    public void e() {

    }

}
