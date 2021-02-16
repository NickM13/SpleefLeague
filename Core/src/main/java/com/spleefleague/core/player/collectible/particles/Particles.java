package com.spleefleague.core.player.collectible.particles;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.vendor.Vendorable;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class Particles extends Collectible {

    public static void init() {
        Vendorable.registerParentType(Particles.class);

        loadCollectibles(Particles.class);
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
