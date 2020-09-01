package com.spleefleague.core.menu.hotbars.main.profile;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

public class PurseMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = InventoryMenuAPI.createItem()
                .setName("Purse")
                .setDisplayItem(Material.CHEST)
                .setDescription("View your various currencies")
                .createLinkedContainer("Purse");

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                .setName(ChatColor.GOLD + "Coins")
                .setDisplayItem(Material.GOLD_NUGGET)
                .setDescription(cp -> "You Have: " + cp.getPurse().getCoins().getAmount())
                .setCloseOnAction(false));

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                .setName(ChatColor.GREEN + "Common Ores")
                .setDisplayItem(Material.COAL_ORE)
                .setDescription(cp -> "You Have: " + cp.getPurse().getCommonOre().getAmount())
                .setCloseOnAction(false));

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                .setName(ChatColor.AQUA + "Rare Ores")
                .setDisplayItem(Material.IRON_ORE)
                .setDescription(cp -> "You Have: " + cp.getPurse().getRareOre().getAmount())
                .setCloseOnAction(false));

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                .setName(ChatColor.DARK_PURPLE + "Epic Ores")
                .setDisplayItem(Material.GOLD_ORE)
                .setDescription(cp -> "You Have: " + cp.getPurse().getEpicOre().getAmount())
                .setCloseOnAction(false));

        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                .setName(ChatColor.YELLOW + "Legendary Ores")
                .setDisplayItem(Material.DIAMOND_ORE)
                .setDescription(cp -> "You Have: " + cp.getPurse().getLegendaryOre().getAmount())
                .setCloseOnAction(false));
    }

    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) init();
        return menuItem;
    }

}
