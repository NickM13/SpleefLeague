/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class CorePlayerOptions extends DBEntity {

    public static class PlayerOptions {

        public static class Option {

            public String displayName;
            public ItemStack displayItem;

            public Option(String displayName, ItemStack displayItem) {
                this.displayName = displayName;
                this.displayItem = displayItem;
            }
        }

        protected List<Option> options;

        public PlayerOptions() {
            options = new ArrayList<>();
        }

        public void addOption(String displayName, ItemStack displayItem) {
            options.add(new Option(displayName, displayItem));
        }

        public List<Option> getOptions() {
            return options;
        }

        public int getOptionCount() {
            return options.size();
        }

    }
    
    public enum CPOptions {
        POST_GAME_WARP
    }
    
    protected static Map<String, PlayerOptions> optionMap;
    
    public static void init() {
        optionMap = new HashMap<>();
        
        PlayerOptions postGameWarp = new PlayerOptions();
        postGameWarp.addOption("Spawn", InventoryMenuUtils.createCustomItem(Material.ACACIA_PLANKS));
        postGameWarp.addOption("Last Location", InventoryMenuUtils.createCustomItem(Material.DARK_OAK_BOAT));
        postGameWarp.addOption("Arena", InventoryMenuUtils.createCustomItem(Material.GRAY_DYE));
        optionMap.put(CPOptions.POST_GAME_WARP.name(), postGameWarp);
    }
    
    public static PlayerOptions getOptions(String name) {
        return optionMap.get(name);
    }
    
    @DBField protected Map<String, Integer> optionManager;
    
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
    
    @DBSave(fieldName ="disabledChats")
    protected List<String> saveDisabledChatChannels() {
        return Lists.newArrayList(disabledChannels);
    }
    @DBLoad(fieldName ="disabledChats")
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