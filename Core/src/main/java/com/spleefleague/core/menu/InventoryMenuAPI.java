/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

/**
 * @author NickM13
 */
public class InventoryMenuAPI {
    
    public static InventoryMenuContainer createContainer() {
        return new InventoryMenuContainer();
    }
    
    /**
     * Creates an InventoryMenuEditor
     *
     * @return New InventoryMenuEditor
     */
    public static InventoryMenuEditor createEditor() {
        return new InventoryMenuEditor();
    }
    
    /**
     * Creates a base InventoryMenuItem
     *
     * @return New InventoryMenuItem
     */
    public static InventoryMenuItem createItem() {
        return new InventoryMenuItem();
    }
    
    /**
     * Creates and registers a new Hotbar item with a permanent slot TODO: should it be permanent?
     * and an identifier which is stored in the "hotbar" nbt
     * accessed by InventoryMenuItemHotbar::getHotbarTag
     *
     * @param slot Slot Number
     * @param hotbarTag "hotbar" NBT String
     * @return New InventoryMenuItemHotbar
     */
    public static InventoryMenuItemHotbar createItemHotbar(int slot, String hotbarTag) {
        return new InventoryMenuItemHotbar(slot, hotbarTag);
    }
    
    public static InventoryMenuItemOption createItemOption(Function<CorePlayer, Integer> selectedFun) {
        return new InventoryMenuItemOption()
                .setSelected(selectedFun);
    }
    
    /**
     * Returns a collection of all hotbar items
     *
     * @return
     */
    public static Collection<InventoryMenuItemHotbar> getHotbarItems() {
        return InventoryMenuItemHotbar.getHotbarItems().values();
    }
    
    /**
     * @param item ItemStack
     * @return Whether item is a registered Hotbar Item
     */
    public static boolean isHotbarItem(ItemStack item) {
        if (item == null) return false;
        return InventoryMenuItemHotbar.isHotbarItem(item);
    }
    
    /**
     * Returns the Hotbar menu item by the hotbar nbt tag, or
     * null if it isn't a Hotbar item
     *
     * @param item ItemStack
     * @return InventoryMenuItemHotbar
     */
    public static InventoryMenuItemHotbar getHotbarItem(ItemStack item) {
        return InventoryMenuItemHotbar.getHotbarItem(item);
    }
    
}
