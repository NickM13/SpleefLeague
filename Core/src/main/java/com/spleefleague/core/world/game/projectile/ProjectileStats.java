/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world.game.projectile;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author NickM13
 */
public class ProjectileStats extends DBEntity {

    public Class<? extends net.minecraft.server.v1_15_R1.Entity> entityClass = FakeEntitySnowball.class;
    @DBField public Integer customModelData = 0;
    @DBField public Double fireRange = 6D;
    @DBField public Integer fireCooldown = 0;
    @DBField public Double breakRadius = 1D;
    @DBField public Double breakPercent = 1D;
    @DBField public Integer bounces = 0;
    @DBField public Double bounciness = 0.3;
    @DBField public Integer lifeTicks = 100;
    @DBField public Integer spread = 0;
    @DBField public Double drag = 1D;
    @DBField public Boolean gravity = true;
    @DBField public Integer count = 1;
    @DBField public Boolean noClip = false;
    @DBField public Boolean collidable = true;
    @DBField public Double hitKnockback = 0D;
    @DBField public Double size = 0D;
    @Deprecated @DBField public Double fireKnockback = 0D;
    private net.minecraft.server.v1_15_R1.ItemStack projectileItem;
    
    public ProjectileStats() {

    }

    public void updateProjectileItem() {
        ItemStack itemStack = new ItemStack(Material.SNOWBALL);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);
        projectileItem = CraftItemStack.asNMSCopy(itemStack);
    }

    public net.minecraft.server.v1_15_R1.ItemStack getProjectileItem() {
        return projectileItem;
    }
    
}
