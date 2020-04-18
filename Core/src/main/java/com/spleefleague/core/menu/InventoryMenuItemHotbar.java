/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * @author NickM13
 */
public class InventoryMenuItemHotbar extends InventoryMenuItem {
    
    public static String getHotbarTag(ItemStack item) {
        if (!item.hasItemMeta()) return "";
        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Core.getInstance(), "hotbar"), PersistentDataType.STRING);
    }
    
    protected int slot;
    protected String hotbarIdentifier;
    
    public InventoryMenuItemHotbar(int slot, String hotbarIdentifier) {
        super();
        
        this.slot = slot;
        this.hotbarIdentifier = hotbarIdentifier;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public String getHotbarIdentifier() {
        return hotbarIdentifier;
    }
    
    @Override
    public ItemStack createItem(CorePlayer cp) {
        ItemStack item = super.createItem(cp);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(new NamespacedKey(Core.getInstance(), "hotbar"), PersistentDataType.STRING, hotbarIdentifier);
        }
        item.setItemMeta(meta);
        return item;
    }
    
}
