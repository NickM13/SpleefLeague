package com.spleefleague.core.player.collectible.pet;

import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;

import java.lang.reflect.InvocationTargetException;

/**
 * @author NickM13
 * @since 4/22/2020
 */
public enum PetType {
    
    OWL("Owl", 92, EntityPetOwl.class, EntityPetOwl::new, EnumCreatureType.CREATURE);
    
    Class<? extends EntityPet> entityClass;
    
    <T extends Entity> PetType(String name, int id, Class<? extends EntityPet> entityClass, EntityTypes.b<T> newFunc, EnumCreatureType creatureType) {
        this.entityClass = entityClass;
        //registerCustomEntity(entityClass, newFunc, creatureType);
        CoreLogger.logError("com.spleefleague.core.player.collectible.pet.PetType.java: Not set up yet!");
    }
    
    private <T extends Entity> void registerCustomEntity(Class<? extends Entity> entityClass,
            EntityTypes.b<T> newFunc,
            EnumCreatureType creatureType) {
        CoreLogger.logInfo("Registered custom entity " + entityClass);
        //EntityTypes.a<Entity> entity = EntityTypes.a.a(newFunc, creatureType);
        //entity.b();
        //IRegistry.a(IRegistry.ENTITY_TYPE, "pettype30", entity.a("pettype31"));
    }
    
    public EntityPet spawn(PetOwner owner) {
        try {
            EntityPet entityPet = entityClass.getDeclaredConstructor(PetOwner.class).newInstance(owner);
            //((CraftWorld) owner.getPlayer().getWorld()).getHandle().addEntity(entityPet);
            return entityPet;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            CoreLogger.logError(exception);
        }
        return null;
    }
    
}
