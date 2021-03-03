package com.spleefleague.core.player.purse;

import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCurrency;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public enum CoreCurrency {

    COIN(ChatColor.GOLD,
            "Coin",
            "Use these at various vendors around the map.",
            InventoryMenuUtils.createCustomItem(Material.GOLD_NUGGET, 1),
            PacketSpigotPlayerCurrency.Type.COIN),
    ORE_COMMON(ChatColor.GREEN,
            "Common Ore",
            "Metal of a questionable quality. Perhaps the local Blacksmith can make something of it...",
            InventoryMenuUtils.createCustomItem(Material.QUARTZ, 2),
            PacketSpigotPlayerCurrency.Type.ORE_COMMON),
    ORE_RARE(ChatColor.AQUA,
            "Rare Ore",
            "Luminescent material from the southern coast, often used as a means of exchange between skilled craftsmen.",
            InventoryMenuUtils.createCustomItem(Material.QUARTZ, 3),
            PacketSpigotPlayerCurrency.Type.ORE_RARE),
    ORE_EPIC(ChatColor.DARK_PURPLE,
            "Epic Ore",
            "The ore radiates with a frozen energy unlike anything ever seen. The enchanters of Borealis may have interest in its quality.",
            InventoryMenuUtils.createCustomItem(Material.QUARTZ, 4),
            PacketSpigotPlayerCurrency.Type.ORE_EPIC),
    ORE_LEGENDARY(ChatColor.YELLOW,
            "Legendary Ore",
            "Forged in the breath of the last great dragon, legend tells of only one skilled enough to work this metal.",
            InventoryMenuUtils.createCustomItem(Material.QUARTZ, 5),
            PacketSpigotPlayerCurrency.Type.ORE_LEGENDARY),
    FRAGMENT_COMMON(ChatColor.GREEN,
            "Common Refined Ore",
            "",
            InventoryMenuUtils.createCustomItem(Material.PRISMARINE_CRYSTALS, 1),
            PacketSpigotPlayerCurrency.Type.FRAGMENT_COMMON),
    FRAGMENT_RARE(ChatColor.AQUA,
            "Rare Refined Ore",
            "",
            InventoryMenuUtils.createCustomItem(Material.PRISMARINE_CRYSTALS, 2),
            PacketSpigotPlayerCurrency.Type.FRAGMENT_RARE),
    FRAGMENT_EPIC(ChatColor.DARK_PURPLE,
            "Epic Refined Ore",
            "",
            InventoryMenuUtils.createCustomItem(Material.PRISMARINE_CRYSTALS, 3),
            PacketSpigotPlayerCurrency.Type.FRAGMENT_EPIC),
    FRAGMENT_LEGENDARY(ChatColor.YELLOW,
            "Legendary Refined Ore",
            "",
            InventoryMenuUtils.createCustomItem(Material.PRISMARINE_CRYSTALS, 4),
            PacketSpigotPlayerCurrency.Type.FRAGMENT_LEGENDARY);

    public ChatColor color;
    public String displayName;
    public String description;
    public ItemStack displayItem;
    public PacketSpigotPlayerCurrency.Type packetType;

    CoreCurrency(ChatColor color, String displayName, String description, ItemStack displayItem, PacketSpigotPlayerCurrency.Type packetType) {
        this.color = color;
        this.displayName = displayName;
        this.description = description;
        this.displayItem = displayItem;
        this.packetType = packetType;
    }

}
