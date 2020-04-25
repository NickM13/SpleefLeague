package com.spleefleague.core.player.collectible.pet;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

/**
 * @author NickM13
 * @since 4/22/2020
 */
public class EntityPet extends EntityInsentient implements EntityBird {
    
    protected PetOwner owner;
    protected NavigationFlying navFlying;
    
    protected EntityPet(EntityTypes<? extends EntityInsentient> entityType, World world) {
        super(entityType, world);
        
        navFlying = new NavigationFlying(this, world);
        navFlying.a(false);
        navFlying.b(true);
        navFlying.d(true);
    }
    
    protected EntityPet(EntityTypes<? extends EntityInsentient> entityType, PetOwner owner) {
        super(entityType, ((CraftWorld) owner.getPlayer().getWorld()).getHandle());
    
        Location loc = owner.getPlayer().getLocation();
        setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
        
        this.owner = owner;
    }
    
    public PetOwner getOwner() {
        return owner;
    }
    
    public NavigationFlying getFlyingNavigation() {
        return navFlying;
    }
    
}
