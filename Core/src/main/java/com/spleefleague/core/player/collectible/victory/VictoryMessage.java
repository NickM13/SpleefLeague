package com.spleefleague.core.player.collectible.victory;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.particles.Particles;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.coreapi.database.annotation.DBField;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class VictoryMessage extends Collectible {

    public static void init() {
        Vendorable.registerParentType(VictoryMessage.class);
    }

    public static void close() {

    }

    @DBField
    private String message = "";

    public void setMessage(String message) {
        this.message = message;
        saveChanges();
    }

    public String getMessage() {
        return this.message;
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
