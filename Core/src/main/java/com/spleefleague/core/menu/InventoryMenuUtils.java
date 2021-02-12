package com.spleefleague.core.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
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

    public enum MenuIcon {
        LOCKED(createCustomItem(Material.STICK, 1)),
        ENABLED(createCustomItem(Material.GREEN_DYE, 1)),
        DISABLED(createCustomItem(Material.RED_DYE, 1)),
        PREVIOUS_GRAY(createCustomItem(Material.GRAY_DYE, 1)),
        NEXT_GRAY(createCustomItem(Material.GRAY_DYE, 2)),
        PREVIOUS(createCustomItem(Material.LIME_DYE, 1)),
        NEXT(createCustomItem(Material.LIME_DYE, 2)),
        RETURN(createCustomItem(Material.BARRIER, 1)),
        CRAFT_GRAY(createCustomItem(Material.IRON_AXE, 1)),
        CRAFT(createCustomItem(Material.IRON_AXE, 2)),
        STORE(createCustomItem(Material.EMERALD, 1));

        ItemStack iconItem;

        MenuIcon(ItemStack iconItem) {
            this.iconItem = iconItem;
        }

        public ItemStack getIconItem() {
            return iconItem;
        }

        public ItemStack getIconItem(int amount) {
            ItemStack itemStack = iconItem.clone();
            itemStack.setAmount(amount);
            return itemStack;
        }

    }

    private static InventoryMenuItem BACK_BUTTON = InventoryMenuAPI.createItemStatic()
            .setName(ChatColor.RED + "" + ChatColor.BOLD + "Return")
            .setDisplayItem(InventoryMenuUtils.MenuIcon.RETURN.getIconItem())
            .setCloseOnAction(false)
            .setAction(cp -> cp.getMenu().onBackButton());

    public static InventoryMenuItem getBackButton() {
        return BACK_BUTTON;
    }

    /**
     * Creates a "locked" menu item with the display name of "Locked"
     *
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
        return InventoryMenuAPI.createItemDynamic()
                .setName(displayName)
                .setDisplayItem(MenuIcon.LOCKED.getIconItem())
                .setCloseOnAction(false);
    }

    /**
     * Creates an unbreakable item with all flags hidden
     *
     * @param displayItem Display Item
     * @return Item Stack
     */
    public static ItemStack createCustomItem(Material displayItem) {
        return createCustomItemAmount(displayItem, 1);
    }

    /**
     * Creates an unbreaking item with all flags hidden and a custom model data value
     *
     * @param displayItem     Display Item
     * @param customModelData Custom Model Data tag
     * @return Item Stack
     */
    public static ItemStack createCustomItem(Material displayItem, int customModelData) {
        return createCustomItemAmount(displayItem, customModelData, 1);
    }

    /**
     * Creates an unbreaking item with all flags hidden and a custom model data value
     *
     * @param displayName     Display Name
     * @param displayItem     Display Item
     * @param customModelData Custom Model Data tag
     * @return Item Stack
     */
    public static ItemStack createCustomItem(String displayName, Material displayItem, int customModelData) {
        ItemStack item = createCustomItemAmount(displayItem, customModelData, 1);
        if (item.getItemMeta() != null) {
            item.getItemMeta().setDisplayName(displayName);
        }
        return item;
    }

    public static ItemStack createCustomItemAmount(Material displayItem, int amount) {
        ItemStack itemStack = new ItemStack(displayItem, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.values());
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public static ItemStack createCustomItemAmount(Material displayItem, int customModelData, int amount) {
        ItemStack itemStack = new ItemStack(displayItem, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setCustomModelData(customModelData);
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.values());
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
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
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (skullMeta != null) skullMeta.setOwningPlayer(op);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static ItemStack createCustomSkull(UUID uuid) {
        if (uuid == null) return InventoryMenuSkullManager.getDefaultSkull();
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta itemMeta = skullItem.getItemMeta();
        if (itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            skullMeta.setOwningPlayer(op);
            skullItem.setItemMeta(skullMeta);
        }
        return skullItem;
    }

    public static ItemStack createCustomSkullOrDefault(UUID uuid) {
        return InventoryMenuSkullManager.getPlayerSkull(uuid);
    }

}
