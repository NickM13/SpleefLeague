/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.vendor;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.annotation.DBField;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * @author NickM13
 */
public class KeyItem extends VendorItem {
    
    private static final Map<String, KeyItem> keyItems = new HashMap<>();
    private static MongoCollection<Document> keyCol;
    
    public static void init() {
        keyCol = Core.getInstance().getPluginDB().getCollection("KeyItems");
        keyCol.find().iterator().forEachRemaining(doc -> {
            KeyItem keyItem = new KeyItem();
            keyItem.load(doc);
            keyItems.put(keyItem.getIdentifier().toLowerCase(), keyItem);
        });
    }
    
    public static void close() {
        keyCol.deleteMany(new Document());
        keyItems.values().forEach(keyItem -> {
            keyItem.saveKeyItem();
        });
    }
    
    public static KeyItem createKeyItem(String name, Integer damage, String displayName) {
        if (!keyItems.containsKey(name)) {
            KeyItem ki = new KeyItem(damage, name, displayName);
            keyItems.put(name, ki);
            ki.saveKeyItem();
            VendorItem.addVendorItem(ki);
        }
        return keyItems.get(name);
    }
    
    public static Map<String, KeyItem> getKeyItems() {
        return keyItems;
    }
    
    public static KeyItem getKeyItem(ItemStack item) {
        if (item.getType().equals(KEY_MATERIAL)) {
            String id = getIdAttribute(item);
            if (id != null) {
                return keyItems.get(id);
            }
        }
        return null;
    }
    public static KeyItem getKeyItem(String key) {
        return keyItems.get(key);
    }
    
    public static Set<String> getKeyItemNames() {
        return keyItems.keySet();
    }
    
    private static final Material KEY_MATERIAL = Material.DIAMOND_AXE;
    
    @DBField
    private String keyName;
    
    public KeyItem() {
        super("key");
        this.material = KEY_MATERIAL;
    }
    
    public KeyItem(Integer damage, String keyName, String name) {
        super("key");
        this.material = KEY_MATERIAL;
        this.damage = damage;
        this.name = name;
        this.description = "";
        this.keyName = keyName;
        this.identifier = keyName;
        VendorItem.addVendorItem(this);
    }
    
    @Override
    public void load(Document doc) {
        super.load(doc);
        this.identifier = this.keyName;
        VendorItem.addVendorItem(this);
    }
    
    public void saveKeyItem() {
        if (keyCol.find(new Document("keyname", keyName)).first() != null) {
            keyCol.deleteMany(new Document("keyname", keyName));
        }
        keyCol.insertOne(save());
    }
    
    private ItemStack applyIdAttribute(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(Core.getInstance(), "keyname"), PersistentDataType.STRING, keyName);
        item.setItemMeta(meta);
        return item;
    }
    
    private static String getIdAttribute(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Core.getInstance(), "keyname"), PersistentDataType.STRING);
    }
    
    public String getKeyName() {
        return keyName;
    }
    
    @Override
    public ItemStack getItem() {
        return applyIdAttribute(super.getItem());
    }
    
    @Override
    public ItemStack getVendorItem() {
        return applyIdAttribute(super.getVendorItem());
    }
    
}
