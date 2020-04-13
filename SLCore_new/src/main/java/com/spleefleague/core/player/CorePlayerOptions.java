/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player;

import com.google.common.collect.Lists;
import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.annotation.DBSave;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.util.database.DBEntity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class CorePlayerOptions extends DBEntity {
    
    public enum CPOptions {
        POST_GAME_WARP
    }
    
    protected static Map<String, PlayerOptions> optionMap;
    
    protected class PostGamePlacement extends PlayerOptions {
        
        public PostGamePlacement() {
            addOption("Spawn", InventoryMenuAPI.createCustomItem(Material.ACACIA_PLANKS));
            addOption("Last Location", InventoryMenuAPI.createCustomItem(Material.DARK_OAK_BOAT));
            addOption("Arena", InventoryMenuAPI.createCustomItem(Material.GRAY_DYE));
        }
        
    }
    
    public static void init() {
        optionMap = new HashMap<>();
        
        PlayerOptions postGameWarp = new PlayerOptions();
        postGameWarp.addOption("Spawn", InventoryMenuAPI.createCustomItem(Material.ACACIA_PLANKS));
        postGameWarp.addOption("Last Location", InventoryMenuAPI.createCustomItem(Material.DARK_OAK_BOAT));
        postGameWarp.addOption("Arena", InventoryMenuAPI.createCustomItem(Material.GRAY_DYE));
        optionMap.put(CPOptions.POST_GAME_WARP.name(), postGameWarp);
    }
    
    public static PlayerOptions getOptions(String name) {
        return optionMap.get(name);
    }
    
    @DBField
    protected Map<String, Integer> optionManager;
    
    protected Set<String> disabledChannels;
    
    public CorePlayerOptions() {
        disabledChannels = new HashSet<>();
        optionManager = new HashMap<>();
        for (CPOptions cpo : CPOptions.values()) {
            optionManager.put(cpo.name(), 0);
        }
    }
    
    public void nextOption(CPOptions cpo) {
        int cid = optionManager.get(cpo.name());
        cid++;
        if (cid >= optionMap.get(cpo.name()).getOptionCount()) {
            cid = 0;
        }
        optionManager.put(cpo.name(), cid);
    }
    
    public int getOption(CPOptions cpo) {
        return optionManager.get(cpo.name());
    }
    
    @DBSave(fieldname="disabledChats")
    protected List<String> saveDisabledChatChannels() {
        return Lists.newArrayList(disabledChannels);
    }
    @DBLoad(fieldname="disabledChats")
    protected void loadDisabledChatChannels(List<String> channels) {
        for (String c : channels) {
            disabledChannels.add(c.toLowerCase());
        }
    }
    
    public void toggleDisabledChannel(String channel) {
        channel = channel.toLowerCase();
        if (disabledChannels.contains(channel)) {
            disabledChannels.remove(channel);
        } else {
            disabledChannels.add(channel);
        }
    }
    public boolean isChannelDisabled(String channel) {
        return disabledChannels.contains(channel.toLowerCase());
    }
    
}
