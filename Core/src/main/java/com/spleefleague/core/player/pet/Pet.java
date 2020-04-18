/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.pet;

import com.spleefleague.core.player.CorePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * PETS PETS PEPREPSTS SERKLRJSPKLHEL:KWJHYALIUUEWYAOIURYGHALOWSEI*U
 * 
 * @author NickM13
 */
public class Pet {

    private static Map<UUID, Pet> playerPets = new HashMap<>();

    public static void setPlayerPet(CorePlayer cp, Pet pet) {
        playerPets.put(cp.getUniqueId(), pet);
    }

    public Pet() {

    }
    
    public void spawnPet(CorePlayer following) {

    }
    
}
