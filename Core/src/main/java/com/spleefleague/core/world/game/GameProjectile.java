/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world.game;

import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.util.variable.RaycastResult;
import java.util.Collections;
import java.util.List;

import com.spleefleague.core.world.FakeProjectile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class GameProjectile {
    
    BoundingBox lastLoc;
    Entity entity;
    Player shooter;
    FakeProjectile type;
    int bounces = 1;
    double bouncePower = 0.3;
    double drag = 1;
    
    public GameProjectile(Entity entity, FakeProjectile type) {
        this.entity = entity;
        this.type = type;
        this.lastLoc = entity.getBoundingBox().clone();
        this.bounces = type.bounces;
        this.bouncePower = type.bounciness;
        this.drag = type.drag;
    }
    
    public double getDrag() {
        return drag;
    }
    
    public boolean doesBounce() {
        return bouncePower > 0;
    }
    
    public void bounce() {
        bounces--;
    }
    
    public boolean hasBounces() {
        return bounces > 0;
    }
    
    public double getBouncePower() {
        return bouncePower;
    }
    
    public FakeProjectile getProjectile() {
        return type;
    }
    
    public void setShooter(Player shooter) {
        this.shooter = shooter;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public List<RaycastResult> cast() {
        if (lastLoc != null) {
            Vector direction = entity.getLocation().toVector().subtract(lastLoc.getCenter());
            lastLoc = entity.getBoundingBox().clone();
            return new Point(lastLoc.getCenter()).cast(direction, direction.length());
        } else {
            lastLoc = entity.getBoundingBox().clone();
            return Collections.EMPTY_LIST;
        }
    }
    
    public void setLastLoc() {
        lastLoc = entity.getBoundingBox().clone();
    }
    
}
