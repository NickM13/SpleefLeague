/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.collectible.pet;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.key.Key;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import org.bson.Document;
import org.bukkit.entity.EntityType;

/**
 * PETS PETS PEPREPSTS SERKLRJSPKLHEL:KWJHYALIUUEWYAOIURYGHALOWSEI*U
 * 
 * @author NickM13
 */
public class Pet extends Collectible {
    
    private static MongoCollection<Document> petCol;
    
    public static void init() {
        Vendorable.registerVendorableType(Pet.class);
        
        petCol = Core.getInstance().getPluginDB().getCollection("Pets");
        petCol.find().iterator().forEachRemaining(doc -> {
            Pet petItem = new Pet();
            petItem.load(doc);
        });
    }
    
    @DBField private EntityType entityType;
    
    public Pet() {
        super();
    }
    
    public void afterLoad() {
        super.afterLoad();
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    
    /**
     * Called when a player clicks on this collectible on
     * their collections menu
     *
     * @param cp Core Player
     */
    @Override
    public void onEnable(CorePlayer cp) {
        cp.sendMessage("Fantastic!");
    }
    
    /**
     * Called when another collectible of the same type has
     * been enabled
     *
     * @param cp Core Player
     */
    @Override
    public void onDisable(CorePlayer cp) {
        cp.sendMessage("Goodbye :(");
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
    
}
