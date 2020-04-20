/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.collectible.key;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.spleefleague.core.Core;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import org.bson.Document;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class Key extends Holdable {
    
    private static MongoCollection<Document> keyCol;
    
    public static void init() {
        Vendorable.registerVendorableType(Key.class);
        
        keyCol = Core.getInstance().getPluginDB().getCollection("KeyItems");
        keyCol.find().iterator().forEachRemaining(doc -> {
            Key keyItem = new Key();
            keyItem.load(doc);
        });
    }
    
    public static void close() {
        Vendorables.getAll(Key.class).forEach((id, key) -> {
            keyCol.deleteMany(new Document("identifer", id));
            keyCol.insertOne(key.save());
        });
    }
    
    /*
    public static Key createKeyItem(String name, Integer damage, String displayName) {
        if (!keyItems.containsKey(name)) {
            Key ki = new Key(damage, name, displayName);
            keyItems.put(name, ki);
            ki.saveKeyItem();
        }
        return keyItems.get(name);
    }
    */
    
    private static final Material DEFAULT_KEY_MAT = Material.DIAMOND_AXE;
    @DBField private Integer damage;
    
    /**
     * Constructor for DB loading
     */
    public Key() {
        super(true);
    }
    
    /**
     * Constructor for use with /key create
     *
     * @param damage Damage
     * @param identifier Identifier String
     * @param name Display Name
     */
    public Key(Integer damage, String identifier, String name) {
        super(true);
        this.identifier = identifier;
        this.name = name;
        this.description = "";
        this.material = DEFAULT_KEY_MAT;
        this.damage = damage;
        this.setDamageNbt(damage);
        this.coinCost = 0;
    }
    
    @Override
    public void afterLoad() {
        this.identifier = String.valueOf(damage);
        this.setDamageNbt(damage);
        super.afterLoad();
    }
    
    public void saveKeyItem() {
        keyCol.deleteMany(new Document("identifier", getIdentifier()));
        keyCol.insertOne(save());
    }
    
    /**
     * Whether an item is available for purchasing for things
     * such as requiring prerequisites, levels or achievements
     *
     * @param cp Core Player
     * @return Availability
     */
    @Override
    public boolean isAvailableToPurchase(CorePlayer cp) {
        return false;
    }
    
    @Override
    public void onRightClick(CorePlayer cp) {
    
    }
    
}
