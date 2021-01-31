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
import java.util.stream.Collectors;

import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class CorePlayerOptions extends DBEntity {
    
    protected Map<String, Integer> optionManager;
    
    protected Set<String> disabledChannels;
    
    public CorePlayerOptions() {
        disabledChannels = new HashSet<>();
    }
    
    public void nextOption(CPOptions cpo) {
        int cid = optionManager.get(cpo.name());
        cid++;
        if (cid >= optionMap.get(cpo.name()).getOptionCount()) {
            cid = 0;
        }
        optionManager.put(cpo.name(), cid);
    }

    @DBLoad(fieldName = "optionManager")
    protected void loadOptionManager(Document doc) {
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            optionManager.put(entry.getKey(), (int) entry.getValue());
        }
    }

    @DBSave(fieldName = "disabledChats")
    protected List<String> saveDisabledChatChannels() {
        return Lists.newArrayList(disabledChannels);
    }

    @DBLoad(fieldName = "disabledChats")
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
