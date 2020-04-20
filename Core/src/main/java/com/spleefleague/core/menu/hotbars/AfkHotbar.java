package com.spleefleague.core.menu.hotbars;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @since 4/18/2020
 */
public class AfkHotbar {
    
    private static InventoryMenuItemHotbar menuItem = null;
    
    public static void init() {
        menuItem = (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(40, "afkitem")
                .setName(ChatColor.RESET + "" + Chat.PLUGIN_PREFIX + "" + ChatColor.BOLD + "AFK")
                .setDescription("You're AFK!")
                .setDisplayItem(Material.DIAMOND_HOE, 253)
                .setAction(cp -> { cp.setInventoryMenuItem(getItemHotbar()); })
                .setAvailability(CorePlayer::isAfk);
    }
    
    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItemHotbar getItemHotbar() {
        if (menuItem == null) init();
        return menuItem;
    }

}
