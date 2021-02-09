package com.spleefleague.core.vendor;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuOverlay;

/**
 * @author NickM13
 * @since 2/9/2021
 */
public class ArtisanOverlay {

    private static InventoryMenuOverlay overlay;

    public static void init() {
        overlay = InventoryMenuAPI.createOverlay()
                .setBackground(119);

    }

    public static InventoryMenuOverlay getOverlay() {
        if (overlay == null) init();
        return overlay;
    }

}
