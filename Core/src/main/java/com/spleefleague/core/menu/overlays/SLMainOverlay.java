package com.spleefleague.core.menu.overlays;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuOverlay;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.main.*;
import com.spleefleague.core.menu.hotbars.main.PartyMenu;
import org.bukkit.ChatColor;

/**
 * @author NickM13
 */
public class SLMainOverlay {

    private static InventoryMenuOverlay overlay;

    public static void init() {
        overlay = InventoryMenuAPI.createOverlay()
                .setBackground("嗰");

        // Options
        overlay.addItem(OptionsMenu.getItem(), 8, 0);

        // Gamemode Menu
        overlay.addItem(GamemodeMenu.getItem(), 7, 1);

        // Leaderboards
        overlay.addItem(LeaderboardMenu.getItem(), 8, 1);

        // Collectibles
        //overlay.addItem(CollectiblesMenu.getItem(), 7, 2);

        // Held Item Selection
        overlay.addItem(HeldItemMenu.getItem(), 7, 2);

        overlay.addItem(CrateMenu.getItem(), 8, 2);

        //overlay.addItem(CompanionsMenu.getItem(), 7, 3);

        //overlay.addItem(ParticlesMenu.getItem(), 8, 3);

        // Profile
        overlay.addItem(ProfileMenu.getItem(), 7, 4);

        overlay.addItem(FriendsMenu.getItem(), 7, 3);

        overlay.addItem(PartyMenu.getItem(), 8, 3);

        // Server Credits
        overlay.addItem(SocialMediaMenu.getItem(), 8, 4);

        // Donor Related
        //overlay.addItem(DonorMenu.getItem(), 7, 5);

        // Moderator Tools
        //overlay.addItem(StaffToolsMenu.getItem(), 8, 5);

        overlay.addItem(InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.RED + "" + ChatColor.BOLD + "Return")
                .setDisplayItem(InventoryMenuUtils.MenuIcon.RETURN.getIconItem())
                .setCloseOnAction(false)
                .setAction(cp -> cp.getMenu().onBackButton()), 7, 0);

        overlay.addItem(InventoryMenuAPI.createItemDynamic()
                        .setName("Previous Page")
                        .setDescription("")
                        .setDisplayItem(cp -> cp.getMenu().hasPagePrevious() ? InventoryMenuUtils.MenuIcon.PREVIOUS.getIconItem() : InventoryMenuUtils.MenuIcon.PREVIOUS_GRAY.getIconItem())
                        .setCloseOnAction(false)
                        .setVisibility(cp -> cp.getMenu().hasPages())
                        .setAction(cp -> cp.getMenu().onPagePrevious()),
                0, 0);

        overlay.addItem(InventoryMenuAPI.createItemDynamic()
                        .setName("Next Page")
                        .setDescription("")
                        .setDisplayItem(cp -> cp.getMenu().hasPageNext() ? InventoryMenuUtils.MenuIcon.NEXT.getIconItem() : InventoryMenuUtils.MenuIcon.NEXT_GRAY.getIconItem())
                        .setCloseOnAction(false)
                        .setVisibility(cp -> cp.getMenu().hasPages())
                        .setAction(cp -> cp.getMenu().onPageNext()),
                4, 0);
    }

    public static InventoryMenuOverlay getOverlay() {
        return overlay;
    }

}
