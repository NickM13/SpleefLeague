package com.spleefleague.core.world.game.projectile;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeWorldPlayer;

/**
 * @author NickM13
 */
public abstract class ProjectileWorldPlayer extends FakeWorldPlayer {

    private long lastHit = 0;

    public ProjectileWorldPlayer(CorePlayer cp) {
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
