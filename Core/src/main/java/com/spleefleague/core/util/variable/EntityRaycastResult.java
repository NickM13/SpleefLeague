package com.spleefleague.core.util.variable;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/16/2020
 */
public class EntityRaycastResult extends RaycastResult {

    private Entity entity;
    private Vector offset;

    EntityRaycastResult(Double distance, Vector intersection, Entity entity) {
        super(distance, intersection);
        this.entity = entity;
        this.offset = intersection.clone().subtract(entity.getLocation().toVector());
    }

    public Entity getEntity() {
        return entity;
    }

    public Vector getOffset() {
        return offset;
    }

}
