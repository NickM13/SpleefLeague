package com.spleefleague.core.player.collectible.pet.pathfinder;

import com.spleefleague.core.player.collectible.pet.EntityPet;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;

/**
 * @author NickM13
 * @since 4/22/2020
 */
public class PathfinderGoalPetFollow extends PathfinderGoal {

    private final double speed;
    private final EntityPet entity;
    private final NavigationAbstract navigation;
    private final float minFollow;

    public PathfinderGoalPetFollow(EntityPet entity, double speed, float minFollow) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
        this.minFollow = minFollow;
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

            PathEntity entityPath = navigation.a(((CraftEntity) entity.getOwner().getPlayer()).getHandle(), 2);
            //PathEntity entityPath = navigation.a(new BlockPosition(loc.getX(), loc.getY(), loc.getZ()), 2);
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
