/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.cosmetics;

import com.spleefleague.core.chat.Chat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

/**
 * @author NickM13
 */
public class CosmeticArmor {
    
    public enum ArmorSlot {
        HELMET(39),
        CHESTPLATE(38),
        LEGGINGS(37),
        BOOTS(36);
        
        int slot;
        
        ArmorSlot(int slot) {
            this.slot = slot;
        }
        
        public int toSlot() {
            return slot;
        }
    }
    
    private static final Map<String, CosmeticArmor> cosmeticArmorsNames = new HashMap<>();
    private static final Map<ItemStack, CosmeticArmor> cosmeticArmorsItems = new HashMap<>();
    
    public static CosmeticArmor SPIN_HAT = new CosmeticArmor("Spin Hat", "Wearing this really stretches your spine!", ArmorSlot.HELMET, Material.DIAMOND_HOE, 228, PotionEffectType.SLOW_FALLING, 1);
    public static CosmeticArmor JUMP_BOOTS = new CosmeticArmor("Spring Loaded Boots", "Hard to find a grip with these!", ArmorSlot.BOOTS, Material.DIAMOND_HOE, 224, PotionEffectType.JUMP, 1);
    
    ArmorSlot armorSlot;
    ItemStack itemStack;
    PotionEffectType potionEffectType;
    int amplitude;
    
    public static CosmeticArmor getArmor(String name) {
        return cosmeticArmorsNames.get(name);
    }
    
    public static CosmeticArmor getArmor(ItemStack itemStack) {
        return cosmeticArmorsItems.get(itemStack);
    }
    
    public static Map<String, CosmeticArmor> getAll() {
        return cosmeticArmorsNames;
    }
    
    public CosmeticArmor(String name, String desc, ArmorSlot armorSlot, Material mat, int damage, PotionEffectType potionEffectType, int amplitude) {
        this.armorSlot = armorSlot;
        itemStack = new ItemStack(mat);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable)
            ((Damageable)itemMeta).setDamage(damage);
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Chat.wrapDescription(desc));
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);
        this.potionEffectType = potionEffectType;
        this.amplitude = amplitude;
        
        cosmeticArmorsNames.put(name, this);
        cosmeticArmorsItems.put(itemStack, this);
    }
    
    public ArmorSlot getArmorSlot() {
        return armorSlot;
    }
    public ItemStack getItem() {
        return itemStack;
    }
    public PotionEffectType getEffectType() {
        return potionEffectType;
    }
    public int getAmplitude() {
        return amplitude;
    }
    
}
