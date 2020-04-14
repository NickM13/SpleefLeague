/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world.projectile;

import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.util.database.DBEntity;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

/**
 * @author NickM13
 */
public class FakeProjectile extends DBEntity {
    
    @DBField
    public EntityType entityType;
    @DBField
    public Integer range;
    @DBField
    public Integer fireRate;
    @DBField
    public Integer power;
    @DBField
    public Integer bounces = 1;
    @DBField
    public Double bounciness = 0.3;
    @DBField
    public Integer lifeTicks = 100;
    @DBField
    public Double spread = 0.;
    @DBField
    public Double drag = 1.;
    @DBField
    public Boolean gravity = true;
    @DBField
    public Material material = Material.BARRIER;
    @DBField
    public Integer damage = 0;
    @DBField
    public Integer count = 1;
    @DBField
    public Double knockback = 0.;
    
    public FakeProjectile() {
        
    }
    
}
