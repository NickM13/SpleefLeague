/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains a static list of all Hotbar items that are created
 * for quick referencing by the custom "hotbar" nbt tag
 *
 * @author NickM13
 */
public class InventoryMenuItemHotbar extends InventoryMenuItem {
    
    // <"hotbar" NBT, Item>
    private static final Map<String, InventoryMenuItemHotbar> HOTBAR_ITEMS = new HashMap<>();
    
    /**
     * Fills a player's hotbar with hotbar items if the item is
     * currently available to them
     *
     * @param cp Core Player
     */
    public static void fillHotbar(CorePlayer cp) {
        Set<String> currentHotbarItems = new HashSet<>();
        if (!cp.getGameMode().equals(GameMode.CREATIVE)) {
            for (int i = 0; i <= 40; i++) {
                ItemStack itemStack = cp.getPlayer().getInventory().getItem(i);
                if (itemStack != null) {
                    if (isHotbarItem(itemStack)) {
                        InventoryMenuItemHotbar hotbarItem = InventoryMenuItemHotbar.getHotbarItem(itemStack);
                        if (hotbarItem != null
                                && hotbarItem.isAvailable(cp)
                                && hotbarItem.createItem(cp).equals(itemStack)
                                && hotbarItem.getSlot() == i) {
                            currentHotbarItems.add(hotbarItem.getHotbarTag());
                            continue;
                        }
                    }
                    cp.getPlayer().getInventory().setItem(i, null);
                }
            }
        } else {
            for (int i = 0; i <= 40; i++) {
                ItemStack itemStack = cp.getPlayer().getInventory().getItem(i);
                if (itemStack != null) {
                    if (isHotbarItem(itemStack)) {
                        InventoryMenuItemHotbar hotbarItem = InventoryMenuItemHotbar.getHotbarItem(itemStack);
                        if (hotbarItem != null
                                && hotbarItem.isAvailable(cp)
                                && hotbarItem.createItem(cp).equals(itemStack)
                                && hotbarItem.getSlot() == i) {
                            currentHotbarItems.add(hotbarItem.getHotbarTag());
                            continue;
                        }
                        cp.getPlayer().getInventory().setItem(i, null);
                    }
                }
            }
        }
        
        for (InventoryMenuItemHotbar hotbarItem : HOTBAR_ITEMS.values()) {
            if (hotbarItem.isAvailable(cp) && !currentHotbarItems.contains(hotbarItem.getHotbarTag())) {
                cp.getPlayer().getInventory().setItem(hotbarItem.getSlot(), hotbarItem.createItem(cp));
            }
        }
    }

    /**
     * Whether an item is a hotbar item or not
     *
     * @param item Item
     * @return Is HotBar Item
     */
    public static boolean isHotbarItem(ItemStack item) {
        return item != null && getHotbarTag(item) != null;
    }
    
    /**
     * Returns a full map of every hotbar item that has
     * been created (stored in constructors)
     *
     * @return Map of Hotbar Items
     */
    public static Map<String, InventoryMenuItemHotbar> getHotbarItems() {
        return HOTBAR_ITEMS;
    }
    
    /**
     * Returns the hotbar item associated with the hotbar tag
     *
     * @param hotbarTag "hotbar" NBT String
     * @return InventoryMenuItemHotbar
     */
    public static InventoryMenuItemHotbar getHotbarItem(String hotbarTag) {
        if (hotbarTag == null) return null;
        return HOTBAR_ITEMS.get(hotbarTag);
    }
    
    /**
     * Returns the hotbar item associated with the hotbar tag of the
     * item passed
     *
     * @param item Item
     * @return InventoryMenuItemHotbar
     */
    public static InventoryMenuItemHotbar getHotbarItem(ItemStack item) {
        return getHotbarItem(InventoryMenuItemHotbar.getHotbarTag(item));
    }
    
    /**
     * Returns the String in the "hotbar" NBT String
     *
     * @param itemStack Item
     * @return "hotbar" NBT String
     */
    public static String getHotbarTag(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        return itemMeta.getPersistentDataContainer().get(new NamespacedKey(Core.getInstance(), "hotbar"), PersistentDataType.STRING);
    }
    
    protected final int slot;
    protected final String hotbarTag;
    
    /**
     * Constructor for Hotbar Items, adds them to the
     * HOTBAR_ITEMS master list on creation
     *
     * @param slot Slot Number
     * @param hotbarTag "hotbar" NBT String
     */
    public InventoryMenuItemHotbar(int slot, String hotbarTag) {
        super();
        this.slot = slot;
        this.hotbarTag = hotbarTag;
        HOTBAR_ITEMS.put(hotbarTag, this);
    }
    
    /**
     * Slot that the hotbar item should be displayed on, can be
     * overridden without issues
     *
     * @return Slot Number
     */
    public int getSlot() {
        return slot;
    }
    
    /**
     * String that is stored in the "hotbar" NBT String of a
     * created Hotbar item
     *
     * @return "hotbar" NBT String
     */
    public String getHotbarTag() {
        return hotbarTag;
    }
    
    /**
     * Creates an item that can be held and clicked by the player
     *
     * @param cp CorePlayer
     * @return Item
     */
    @Override
    public ItemStack createItem(CorePlayer cp) {
        ItemStack item = super.createItem(cp);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(new NamespacedKey(Core.getInstance(), "hotbar"), PersistentDataType.STRING, hotbarTag);
        }
        item.setItemMeta(meta);
        return item;
    }
    
    @Override
    public void callAction(CorePlayer cp) {
        if (getLinkedContainer() != null) {
            cp.setInventoryMenuChest(getLinkedContainer(), true);
        }
        super.callAction(cp);
    }
    
}
