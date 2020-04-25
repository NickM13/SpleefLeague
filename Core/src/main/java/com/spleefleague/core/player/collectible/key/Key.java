/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.collectible.key;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;

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
    
    public static Key createKeyItem(String identifier, String name, Integer damage) {
        Key key;
        if ((key = Vendorables.get(Key.class, identifier)) != null) {
            return key;
        }
        key = new Key(identifier, name, damage);
        Vendorables.register(key);
        return key;
    }
    
    private static final Material DEFAULT_KEY_MAT = Material.DIAMOND_AXE;
    
    /**
     * Constructor for DB loading
     */
    public Key() {
        super(true);
    }
    
    /**
     * Constructor for use with /key create
     *
     * @param identifier Identifier String
     * @param name Display Name
     * @param damage Damage
     */
    public Key(String identifier, String name, Integer damage) {
        super(true);
        this.identifier = identifier;
        this.name = name;
        this.description = "";
        this.material = DEFAULT_KEY_MAT;
        this.setDamageNbt(damage);
        this.coinCost = 0;
        updateDisplayItem();
    }
    
    @Override
    public void afterLoad() {
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
