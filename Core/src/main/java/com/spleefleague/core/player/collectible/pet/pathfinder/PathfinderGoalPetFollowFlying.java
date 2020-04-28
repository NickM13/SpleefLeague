package com.spleefleague.core.player.collectible.pet.pathfinder;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.pet.EntityPet;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;

/**
 * @author NickM13
 * @since 4/22/2020
 */
public class PathfinderGoalPetFollowFlying extends PathfinderGoal {
    
    private final double speed;
    private final EntityPet entity;
    private final NavigationFlying navigation;
    private final float minFollow;
    
    public PathfinderGoalPetFollowFlying(EntityPet entity, double speed, float minFollow) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getFlyingNavigation();
        this.minFollow = minFollow;
    }
    
    private double ownerDist() {
        Location loc = entity.getOwner().getPlayer().getLocation();
        return entity.getPositionVector().distanceSquared(new Vec3D(loc.getX(), loc.getY(), loc.getZ()));
    }
    
    @Override
    public boolean a() {
        return entity.getOwner() != null
                && ownerDist() > (minFollow * minFollow)
                && entity.getOwner().getCorePlayer().isFlying();
    }
    
    @Override
    public boolean b() {
        if (entity.getOwner() != null) {
            return entity.getOwner().getCorePlayer().isFlying();
        }
        return false;
    }
    
    @Override
    public void c() {
    
    }
    
    @Override
    public void d() {
    
    }
    
    @Override
    public void e() {
    
    }
    
}
