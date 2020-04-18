/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.collectible.pet;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * PETS PETS PEPREPSTS SERKLRJSPKLHEL:KWJHYALIUUEWYAOIURYGHALOWSEI*U
 * 
 * @author NickM13
 */
public class Pet extends Collectible {

    public Pet() {
        super("PET");
    }
    
    /**
     * Called when a player clicks on this collectible on
     * their collections menu
     */
    @Override
    public void onEnable() {
    
    }
    
    /**
     * Called when another collectible of the same type has
     * been enabled
     */
    @Override
    public void onDisable() {
    
    }
    
    /**
     * Whether an item is available for purchasing for things
     * such as requiring prerequisites, levels or achievements
     *
     * @param cp Core Player
     * @return Availability
     */
    @Override
    public boolean isAvailable(CorePlayer cp) {
        return false;
    }
}
