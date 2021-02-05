package com.spleefleague.core.player.purse;

import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public enum CoreCurrency {

    COIN(ChatColor.GOLD, "Coin", InventoryMenuUtils.createCustomItem(Material.GOLD_NUGGET, 1)),
    ORE_COMMON(ChatColor.GREEN, "Common Ore", InventoryMenuUtils.createCustomItem(Material.QUARTZ, 2)),
    ORE_EPIC(ChatColor.DARK_PURPLE, "Epic Ore", InventoryMenuUtils.createCustomItem(Material.QUARTZ, 3)),
    ORE_LEGENDARY(ChatColor.YELLOW, "Legendary Ore", InventoryMenuUtils.createCustomItem(Material.QUARTZ, 4)),
    ORE_RARE(ChatColor.AQUA, "Rare Ore", InventoryMenuUtils.createCustomItem(Material.QUARTZ, 5));

    public ChatColor color;
    public String displayName;
    public ItemStack displayItem;

    CoreCurrency(ChatColor color, String displayName, ItemStack displayItem) {
        this.color = color;
        this.displayName = displayName;
        this.displayItem = displayItem;
    }

}
