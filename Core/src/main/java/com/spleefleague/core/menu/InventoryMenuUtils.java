package com.spleefleague.core.menu;

import org.bukkit.Bukkit;
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

import java.util.UUID;

/**
 * Quick access functions for creating various specific types
 * of items that have editable Meta Data
 *
 * @author NickM13
 * @since 4/18/2020
 */
public class InventoryMenuUtils {
    
    private static final ItemStack LOCKED_ICON = createCustomItem(Material.DIAMOND_AXE, 12);
    
    /**
     * Returns the locked icon used in locked menu items
     *
     * @return Item Stack
     */
    public static ItemStack getLockedIcon() {
        return LOCKED_ICON;
    }
    
    /**
     * Creates a "locked" menu item with the display name of "Locked"
     * @return Locked InventoryMenuItem
     */
    public static InventoryMenuItem createLockedMenuItem() {
        return createLockedMenuItem("Locked");
    }
    
    /**
     * Creates a "locked" menu item
     *
     * @param displayName Display Name
     * @return Locked InventoryMenuItem
     */
    public static InventoryMenuItem createLockedMenuItem(String displayName) {
        return InventoryMenuAPI.createItem()
                .setName(displayName)
                .setDisplayItem(LOCKED_ICON)
                .setCloseOnAction(false);
    }
    
    /**
     * Creates an unbreakable item with all flags hidden
     *
     * @param displayItem Display Item
     * @return Item Stack
     */
    public static ItemStack createCustomItem(Material displayItem) {
        ItemStack itemStack = new ItemStack(displayItem);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.values());
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    
    /**
     * Creates an unbreaking item with all flags hidden and a damage value
     *
     * @param displayItem Display Item
     * @param damage Damage Value
     * @return Item Stack
     */
    public static ItemStack createCustomItem(Material displayItem, int damage) {
        ItemStack itemStack = new ItemStack(displayItem);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (itemMeta instanceof Damageable) {
                ((Damageable) itemMeta).setDamage(damage);
            }
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.values());
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    public static ItemStack createCustomItem(String name, Material displayItem, int damage) {
        ItemStack item = createCustomItem(displayItem, damage);
        if (item.getItemMeta() != null)
            item.getItemMeta().setDisplayName(name);
        return item;
    }
    
    public static ItemStack createCustomPotion(PotionType pt) {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        if (potionMeta != null)
            potionMeta.setBasePotionData(new PotionData(pt));
        item.setItemMeta(potionMeta);
        return item;
    }
    
    public static ItemStack createCustomSkull(String playerName) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
        if (skullMeta != null) skullMeta.setOwningPlayer(op);
        skull.setItemMeta(skullMeta);
        return skull;
    }
    
    public static ItemStack createCustomSkull(UUID uuid) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
        if (skullMeta != null) skullMeta.setOwningPlayer(op);
        skull.setItemMeta(skullMeta);
        return skull;
    }
    
}
