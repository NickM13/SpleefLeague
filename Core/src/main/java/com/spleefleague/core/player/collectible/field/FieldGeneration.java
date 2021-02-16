package com.spleefleague.core.player.collectible.field;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.vendor.Vendorable;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class FieldGeneration extends Collectible {

    public static void init() {
        Vendorable.registerParentType(FieldGeneration.class);

        loadCollectibles(FieldGeneration.class);
    }

    public static void close() {

    }

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
