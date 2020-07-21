/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.collectible.pet;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.coreapi.database.annotation.DBField;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * PETS PETS PEPREPSTS SERKLRJSPKLHEL:KWJHYALIUUEWYAOIURYGHALOWSEI*U
 * 
 * @author NickM13
 */
public class Pet extends Collectible {
    
    private static final Map<CorePlayer, PetOwner> playerPetMap = new HashMap<>();
    private static final Map<UUID, CorePlayer> petPlayerMap = new HashMap<>();
    
    public static void init() {
        Vendorable.registerVendorableType(Pet.class);
    }
    
    public static void close() {
        playerPetMap.keySet().forEach(Pet::killPet);
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
    
    protected static void killPet(CorePlayer cp) {
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

    /**
     * Constructor for use with /pet create
     *
     * @param identifier Identifier String
     * @param name Display Name
     */
    public Pet(String identifier, String name) {
        super();
        this.identifier = identifier;
        this.name = name;
        this.material = Material.WOLF_SPAWN_EGG;
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
        killPet(cp);
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
