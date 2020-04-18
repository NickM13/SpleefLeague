/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.vendor;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.database.variable.DBEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author NickM13
 */
public class VendorItem extends DBEntity {
    
    // <Type, <ID, Item>>
    protected static Map<String, Map<String, VendorItem>> vendorItems = new HashMap<>();
    protected static Map<String, VendorItem> vendorItemsFull = new HashMap<>();
    
    private static String toSimpleItem(ItemStack item) {
        int damage = 0;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            damage = ((Damageable) meta).getDamage();
        }
        return item.getType().toString() + damage;
    }
    
    public static Set<String> getItemTypes() {
        return vendorItems.keySet();
    }
    
    public static Map<String, VendorItem> getItems(String name) {
        return vendorItems.get(name);
    }
    public static VendorItem getVendorItem(String type, String id) {
        if (!vendorItems.containsKey(type)) return null;
        return vendorItems.get(type).get(id);
    }
    public static VendorItem getVendorItem(ItemStack item) {
        return vendorItemsFull.get(toSimpleItem(item));
    }
    public static void addVendorItem(VendorItem item) {
        if (!vendorItems.containsKey(item.getType())) {
            vendorItems.put(item.getType(), new HashMap<>());
        }
        vendorItems.get(item.getType()).put(item.getIdentifier(), item);
        vendorItemsFull.put(toSimpleItem(item.getItem()), item);
    }
    public static boolean removeVendorItem(VendorItem item) {
        if (vendorItems.containsKey(item.getType())) {
            if (vendorItems.get(item.getType()).containsKey(item.getIdentifier())) {
                vendorItems.get(item.getType()).remove(item.getIdentifier());
                return true;
            }
        }
        return false;
    }
    
    protected Material material;
    @DBField
    protected Integer damage;
    @DBField
    protected String name;
    protected String identifier;
    @DBField
    protected String description;
    @DBField
    protected Integer coinCost;
    protected String type;
    
    public VendorItem(String type) {
        coinCost = 0;
        this.type = type;
    }
    
    /**
     * For creating a temporary vendor item
     * Used for Held Item hotbar menu item
     * @param identifier
     * @param material
     * @param damage
     * @param name
     * @param description
     */
    public VendorItem(String identifier, Material material, Integer damage, String name, String description) {
        this.identifier = identifier;
        this.material = material;
        this.damage = damage;
        this.name = name;
        this.description = description;
    }
    
    @Override
    public void load(Document doc) {
        super.load(doc);
        if (this.identifier == null) {
            identifier = name;
        }
    }
    
    @DBLoad(fieldName ="material")
    protected void loadMaterial(String str) {
        material = Material.getMaterial(str);
    }
    
    public void setDamage(int damage) {
        this.damage = damage;
    }
    public int getDamage() {
        return damage;
    }
    public String getIdentifier() {
        return identifier;
    }
    public Material getMaterial() {
        return material;
    }
    public void setDisplayName(String name) {
        this.name = name;
    }
    public String getDisplayName() {
        return name;
    }
    public String getVendorDescription() {
        String desc = description;
        desc += "\n\n" + ChatColor.AQUA + "Cost: " + ChatColor.GOLD + getCoinCost();
        return desc;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
    public void setCoinCost(int coinCost) {
        this.coinCost = coinCost;
    }
    public int getCoinCost() {
        return coinCost;
    }
    
    public String getType() {
        return type;
    }
    
    public boolean isUnlocked(CorePlayer cp) {
        return false;
    }
    public boolean isPurchaseable(CorePlayer cp) {
        return (cp.getCoins() >= this.getCoinCost());
    }
    public void purchase(CorePlayer cp) {
        
    }
    
    public ItemStack getItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(damage);
        }
        itemMeta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Chat.wrapDescription(description));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public ItemStack getVendorItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(damage);
        }
        itemMeta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Chat.wrapDescription(getVendorDescription()));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public void activate(CorePlayer cp) {
        
    }
    
}
