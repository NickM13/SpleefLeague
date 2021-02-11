package com.spleefleague.core.world.game;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeWorldPlayer;
import com.spleefleague.core.world.game.projectile.ProjectileWorldPlayer;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class GameWorldPlayer extends ProjectileWorldPlayer {

    private long lastHit = 0;
    private long lastPortal = 0;

    public GameWorldPlayer(CorePlayer cp) {
        super(cp);
    }

    public boolean hit() {
        if (lastHit < System.currentTimeMillis()) {
            lastHit = System.currentTimeMillis() + 250;
            return true;
        }
        return false;
    }

    public boolean portal() {
        if (lastPortal < System.currentTimeMillis()) {
            lastPortal = System.currentTimeMillis() + 250;
            return true;
        }
        return false;
    }
    
}
