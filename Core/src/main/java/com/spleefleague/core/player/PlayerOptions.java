/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import com.spleefleague.core.menu.InventoryMenuItemOption;

/**
 * @author NickM13
 */
public class PlayerOptions {
    
    public class Option {
        
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
