package com.spleefleague.core.world.game;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeWorldPlayer;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class GameWorldPlayer extends FakeWorldPlayer {

    private long lastHit = 0;

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
    
}
