package com.spleefleague.core.menu.hotbars;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/18/2020
 */
public class SLMainHotbar {

    private static InventoryMenuItemHotbar menuItem = null;

    public static void init() {
        menuItem = (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(0, "mainMenu")
                .setName(ChatColor.RESET + "" + Chat.TAG + "" + ChatColor.BOLD + "SpleefLeague Menu")
                .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.NETHER_STAR, 1))
                .setAvailability(CorePlayer::isMenuAvailable)
                .createLinkedContainer("SpleefLeague");
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
