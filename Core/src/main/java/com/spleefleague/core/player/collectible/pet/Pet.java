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
import com.spleefleague.core.vendor.Vendorable;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * PETS PETS PEPREPSTS SERKLRJSPKLHEL:KWJHYALIUUEWYAOIURYGHALOWSEI*U
 * 
 * @author NickM13
 */
public class Pet extends Collectible {
    
    private static MongoCollection<Document> petCol;
    
    private static final Map<CorePlayer, PetOwner> playerPetMap = new HashMap<>();
    private static final Map<UUID, CorePlayer> petPlayerMap = new HashMap<>();
    
    public static void init() {
        Vendorable.registerVendorableType(Pet.class);
        
        petCol = Core.getInstance().getPluginDB().getCollection("Pets");
        petCol.find().iterator().forEachRemaining(doc -> {
            Pet petItem = new Pet();
            petItem.load(doc);
        });
    }
    
    public static void close() {
        playerPetMap.keySet().forEach(Pet::despawnPet);
    }
    
    protected static void spawnPet(CorePlayer cp, Pet pet) {
        PetOwner petOwner;
        if (!playerPetMap.containsKey(cp)) {
            petOwner = new PetOwner(cp);
            playerPetMap.put(cp, petOwner);
        } else {
            petOwner = playerPetMap.get(cp);
        }
        EntityPet entity = pet.getPetType().spawn(petOwner);
        if (entity != null) {
            petOwner.setEntityPet(entity);
            petPlayerMap.put(entity.getUniqueID(), cp);
        }
    }
    
    protected static void despawnPet(CorePlayer cp) {
        if (!playerPetMap.containsKey(cp)) return;
        if (playerPetMap.get(cp).getEntityPet() == null) return;
        petPlayerMap.remove(playerPetMap.get(cp).getEntityPet().getUniqueID());
        playerPetMap.get(cp).getEntityPet().killEntity();
        playerPetMap.get(cp).setEntityPet(null);
    }
    
    @DBField private PetType petType;
    
    public Pet() {
        super();
    }
    
    public void afterLoad() {
        super.afterLoad();
    }
    
    public PetType getPetType() {
        return petType;
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
        spawnPet(cp, this);
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
        despawnPet(cp);
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
