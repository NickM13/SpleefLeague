package com.spleefleague.core.player.collectible.pet;

import com.spleefleague.core.util.CoreUtils;
import net.minecraft.server.v1_15_R1.PathfinderGoalSelector;

import java.util.List;

/**
 * @author NickM13
 * @since 4/22/2020
 */
public class PetUtils {
    
    public static void clearListField(String fieldName, Class<?> clazz, Object object) {
        List<?> list = (List<?>) CoreUtils.getPrivateField(fieldName, clazz, object);
        if (list != null) list.clear();
    }
    
    public static void clearPathfinderGoals(PathfinderGoalSelector pathfinder) {
        PetUtils.clearListField("b", PathfinderGoalSelector.class, pathfinder);
        PetUtils.clearListField("c", PathfinderGoalSelector.class, pathfinder);
    }
    
}
