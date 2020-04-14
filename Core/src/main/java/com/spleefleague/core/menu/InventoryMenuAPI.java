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
    
    private static ItemStack LOCKED_ICON = createCustomItem(Material.DIAMOND_AXE, 12);
    
    public static InventoryMenuItem createLockedMenuItem() {
        return createLockedMenuItem("Locked");
    }
    public static InventoryMenuItem createLockedMenuItem(String name) {
        return InventoryMenuAPI.createItem()
                .setName(name)
                .setDisplayItem(LOCKED_ICON)
                .setCloseOnAction(false);
    }
    public static ItemStack getLockedIcon() {
        return LOCKED_ICON;
    }
    public static ItemStack createCustomItem(Material displayItem) {
        ItemStack itemStack = new ItemStack(displayItem);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    public static ItemStack createCustomItem(Material displayItem, int damage) {
        ItemStack itemStack = new ItemStack(displayItem);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) {
            ((Damageable)itemMeta).setDamage(damage);
        }
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    public static ItemStack createCustomItem(String name, Material displayItem, int damage) {
        ItemStack item = createCustomItem(displayItem, damage);
        item.getItemMeta().setDisplayName(name);
        return item;
    }
    
    public static ItemStack createCustomPotion(PotionType pt) {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setBasePotionData(new PotionData(pt));
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createCustomSkull(String playerName) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullmeta = (SkullMeta)skull.getItemMeta();
        skullmeta.setOwningPlayer(op);
        skull.setItemMeta(skullmeta);
        return skull;
    }
    
    public static ItemStack createCustomSkull(UUID uuid) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullmeta = (SkullMeta)skull.getItemMeta();
        skullmeta.setOwningPlayer(op);
        skull.setItemMeta(skullmeta);
        return skull;
    }
    
    public static InventoryMenuContainer createContainer() {
        return new InventoryMenuContainer();
    }
    
    public static InventoryMenuEditor createEditor() {
        return new InventoryMenuEditor();
    }
    
    public static InventoryMenuItem createItem() {
        return new InventoryMenuItem();
    }
    
    public static InventoryMenuItemHotbar createItemHotbar(int slot, String identifier) {
        return new InventoryMenuItemHotbar(slot, identifier);
    }
    
    public static InventoryMenuItemOption createItemOption(Function<CorePlayer, Integer> selectedFun) {
        return new InventoryMenuItemOption()
                .setSelected(selectedFun);
    }
    
    public static boolean isHotbarItem(ItemStack item) {
        return item != null && InventoryMenuItemHotbar.getHotbarTag(item) != null;
    }
    
    public static Collection<InventoryMenuItemHotbar> getHotbarItems() {
        return hotbarItems.values();
    }
    
    public enum InvMenuType {
        SLMENU(0),
        HELD(8);
        
        int slot;
        
        InvMenuType(int slot) {
            this.slot = slot;
        }
        
        public int getSlot() {
            return slot;
        }
        
        public InventoryMenuItemHotbar create() {
            return createItemHotbar(slot, name().toLowerCase());
        }
    }
    
    private static Map<String, InventoryMenuItemHotbar> hotbarItems = new HashMap<>();
    
    public static InventoryMenuItemHotbar getHotbarItem(InvMenuType type) {
        return hotbarItems.get(type.name().toLowerCase());
    }
    public static InventoryMenuItemHotbar getHotbarItem(ItemStack item) {
        String hotbarName;
        if ((hotbarName = InventoryMenuItemHotbar.getHotbarTag(item)) != null) {
            return hotbarItems.get(hotbarName);
        }
        return null;
    }
    
    public static void init() {
        hotbarItems.put(InvMenuType.SLMENU.name().toLowerCase(), (InventoryMenuItemHotbar) InvMenuType.SLMENU.create()
                .setName(ChatColor.RESET + "" + Chat.PLUGIN_PREFIX + "" + ChatColor.BOLD + "SpleefLeague Menu")
                .setDisplayItem(new ItemStack(Material.COMPASS))
                .setAction(cp -> { cp.setInventoryMenuItem(hotbarItems.get(InvMenuType.SLMENU.name().toLowerCase())); })
                .createLinkedContainer("SpleefLeague Menu"));
        
        hotbarItems.put(InvMenuType.HELD.name().toLowerCase(), (InventoryMenuItemHotbar) InvMenuType.HELD.create()
                .setName(cp -> cp.getHeldItem().getDisplayName())
                .setDisplayItem(cp -> cp.getHeldItem().getItem())
                .setDescription(cp -> cp.getHeldItem().getDescription())
                .setVisibility(cp -> cp.hasSelectedHeldItem())
                .setAction(cp -> { cp.activateHeldItem(); }));
    }
    
}
