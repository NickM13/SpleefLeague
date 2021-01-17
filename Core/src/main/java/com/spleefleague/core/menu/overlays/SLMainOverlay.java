package com.spleefleague.core.menu.overlays;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuOverlay;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.main.*;
import org.bukkit.ChatColor;

/**
 * @author NickM13
 */
public class SLMainOverlay {

    private static InventoryMenuOverlay overlay;

    public static void init() {
        overlay = new InventoryMenuOverlay();

        // Collectibles
        overlay.addItem(CollectiblesMenu.getItem(), 7, 2);

        // Profile
        overlay.addItem(ProfileMenu.getItem(), 7, 4);

        // Held Item Selection
        overlay.addItem(HeldItemMenu.getItem(), 8, 2);

        // Leaderboards
        overlay.addItem(LeaderboardMenu.getItem(), 8, 1);

        // Options
        overlay.addItem(OptionsMenu.getItem(), 8, 0);

        // Donor Related
        overlay.addItem(DonorMenu.getItem(), 8, 5);

        // Server Credits
        overlay.addItem(CreditsMenu.getItem(), 8, 4);

        // Moderator Tools
        overlay.addItem(StaffToolsMenu.getItem(), 8, 1);

        overlay.addItem(InventoryMenuAPI.createItem()
                .setName(ChatColor.RED + "" + ChatColor.BOLD + "Return")
                .setDisplayItem(InventoryMenuUtils.MenuIcon.RETURN.getIconItem())
                .setCloseOnAction(false)
                .setAction(cp -> cp.getMenu().onBackButton()), 7, 0);

        overlay.addItem(InventoryMenuAPI.createItem()
                .setName("Previous Page")
                .setDescription("")
                .setDisplayItem(cp -> cp.getMenu().hasPageNext() ? InventoryMenuUtils.MenuIcon.PREVIOUS.getIconItem() : InventoryMenuUtils.MenuIcon.PREVIOUS_GRAY.getIconItem())
                .setCloseOnAction(false)
                .setAction(cp -> cp.getMenu().onPageNext()),
                0, 0);

        overlay.addItem(InventoryMenuAPI.createItem()
                .setName("Next Page")
                .setDescription("")
                .setDisplayItem(cp -> cp.getMenu().hasPagePrevious() ? InventoryMenuUtils.MenuIcon.NEXT.getIconItem() : InventoryMenuUtils.MenuIcon.NEXT_GRAY.getIconItem())
                .setCloseOnAction(false)
                .setAction(cp -> cp.getMenu().onPagePrevious()),
                4, 0);
    }

    public static InventoryMenuOverlay getOverlay() {
        return overlay;
    }

}
