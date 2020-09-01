package com.spleefleague.core.menu.hotbars;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.main.*;
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
                .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.COMPASS, 1))
                .setAvailability(CorePlayer::isMenuAvailable)
                .createLinkedContainer("SpleefLeague Menu");
    
        // Collectibles
        menuItem.getLinkedChest()
                .addMenuItem(CollectiblesMenu.getItem(), 0, 2);
    
        // Profile
        menuItem.getLinkedChest()
                .addMenuItem(ProfileMenu.getItem(), 1, 2);
    
        // Held Item Selection
        menuItem.getLinkedChest()
                .addMenuItem(HeldItemMenu.getItem(), 0, 3);
    
        // Leaderboards
        menuItem.getLinkedChest()
                .addMenuItem(LeaderboardMenu.getItem(), 7, 2);
    
        // Options
        menuItem.getLinkedChest()
                .addMenuItem(OptionsMenu.getItem(), 7, 3);
    
        // Donor Related
        menuItem.getLinkedChest()
                .addMenuItem(DonorMenu.getItem(), 8, 2);
        
        // Server Credits
        menuItem.getLinkedChest()
                .addMenuItem(CreditsMenu.getItem(), 8, 3);
    
        // Moderator Tools
        menuItem.getLinkedChest()
                .addMenuItem(StaffToolsMenu.getItem(), 8, 1);
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
