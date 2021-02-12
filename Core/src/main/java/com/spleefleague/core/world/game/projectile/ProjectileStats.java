/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world.game.projectile;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author NickM13
 */
public class ProjectileStats extends DBEntity {

    public enum BreakStyle {
        DEFAULT, CORROSIVE, FIRE, ICE, REGENERATE
    }

    public enum Shape {
        DEFAULT, CYLINDER, CONE, PLUS
    }

    public enum FireSystem {
        DEFAULT, CHARGE
    }

    public Class<? extends net.minecraft.server.v1_15_R1.Entity> entityClass = FakeEntitySnowball.class;
    @DBField
    public BreakStyle breakStyle = BreakStyle.DEFAULT;
    @DBField
    public FireSystem fireSystem = FireSystem.DEFAULT;
    @DBField
    public Integer chargeTime = 20;
    @DBField
    public Sound chargedSoundEffect = Sound.BLOCK_ANVIL_LAND;
    @DBField
    public Double chargedSoundVolume = 1.;
    @DBField
    public Double chargedSoundPitch = 1.;
    @DBField
    public Sound chargingSoundEffect = Sound.BLOCK_ANVIL_STEP;
    @DBField
    public Double chargingSoundVolume = 1.;
    @DBField
    public Double chargingSoundPitch = 1.;
    @DBField
    public Sound soundEffect = Sound.ENTITY_SNOWBALL_THROW;
    @DBField
    public Double soundVolume = 1.;
    @DBField
    public Double soundPitch = 1.;
    @DBField
    public Shape shape = Shape.DEFAULT;
    @DBField
    public List<Integer> customModelDatas = new ArrayList<>(0);
    @DBField
    public Double fireRange = 6D;
    @DBField
    public Integer fireCooldown = 0;
    @DBField
    public Double breakRadius = 1D;
    @DBField
    public Double breakPercent = 1D;
    @DBField
    public Integer bounces = 0;
    @DBField
    public Double bounciness = 0.3;
    @DBField
    public Integer breakAfterBounces = 0;
    @DBField
    public Integer lifeTicks = 100;
    @DBField
    public Integer hSpread = 0;
    @DBField
    public Integer vSpread = 0;
    @DBField
    public Double drag = 1D;
    @DBField
    public Boolean gravity = true;
    @DBField
    public Integer count = 1;
    @DBField
    public Integer repeat = 0;
    @DBField
    public Integer repeatDelay = 0;
    @DBField
    public Boolean noClip = false;
    @DBField
    public Boolean collidable = true;
    @DBField
    public Double hitKnockback = 0D;
    @DBField
    public Double size = 0D;
    @DBField
    public Double fireKnockback = 0D;
    private net.minecraft.server.v1_15_R1.ItemStack projectileItem;

    public ProjectileStats() {

    }

    public void updateProjectileItem() {
        ItemStack itemStack = new ItemStack(Material.SNOWBALL);
        ItemMeta itemMeta = itemStack.getItemMeta();
        Objects.requireNonNull(itemMeta).setCustomModelData(customModelDatas.isEmpty() ? 0 : customModelDatas.get(0));
        itemStack.setItemMeta(itemMeta);
        projectileItem = CraftItemStack.asNMSCopy(itemStack);
    }

    public net.minecraft.server.v1_15_R1.ItemStack getProjectileItem() {
        return projectileItem;
    }

}
