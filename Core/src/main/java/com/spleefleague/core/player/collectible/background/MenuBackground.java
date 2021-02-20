package com.spleefleague.core.player.collectible.background;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.coreapi.database.annotation.DBField;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class MenuBackground extends Collectible {

    public static void init() {
        Vendorable.registerParentType(MenuBackground.class);

        loadCollectibles(MenuBackground.class);
    }

    public static void close() {

    }

    @DBField
    private String background;

    @Override
    public void onEnable(CorePlayer cp) {

    }

    @Override
    public void onDisable(CorePlayer cp) {

    }

    @Override
    public boolean isAvailableToPurchase(CorePlayer cp) {
        return false;
    }

}
