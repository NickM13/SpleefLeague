package com.spleefleague.core.world.global.vehicle;

import com.spleefleague.core.menu.InventoryMenuUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/3/2020
 */
public class LaunchPad extends GlobalVehicle {
    
    public static void launchEntity(Vector pos, Vector vec, LivingEntity livingEntity) {
        livingEntity.getWorld().spawn(new Location(livingEntity.getWorld(),
                        pos.getX(), pos.getY(), pos.getZ()),
                Snowball.class, (entity) -> {
                    entity.setItem(InventoryMenuUtils.createCustomItem(Material.SNOWBALL, 1));
                    livingEntity.setCollidable(false);
                    entity.setVelocity(vec);
                    entity.addPassenger(livingEntity);
                    entityMap.put(entity.getEntityId(), livingEntity);
                });
        livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1);
    }
    
}
