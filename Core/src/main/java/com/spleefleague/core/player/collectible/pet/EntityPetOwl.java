package com.spleefleague.core.player.collectible.pet;

import com.spleefleague.core.player.collectible.pet.pathfinder.PathfinderGoalPetFollow;
import com.spleefleague.core.player.collectible.pet.pathfinder.PathfinderGoalPetFollowFlying;
import net.minecraft.server.v1_15_R1.*;

/**
 * @author NickM13
 * @since 4/22/2020
 */
public class EntityPetOwl extends EntityPet {
    
    public EntityPetOwl(EntityTypes<Entity> entityTypes, World world) {
        super(EntityTypes.COW, world);
    }
    
    public EntityPetOwl(PetOwner owner) {
        super(EntityTypes.COW, owner);
    }
    
    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        //this.goalSelector.a(1, new PathfinderGoalPetFollowFlying(this, 2, 5));
        this.goalSelector.a(2, new PathfinderGoalPetFollow(this, 2, 5));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    /*
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(20);
    }
     */
    
    public PetOwner getOwner() {
        return owner;
    }
    
}
