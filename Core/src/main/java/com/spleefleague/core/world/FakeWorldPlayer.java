package com.spleefleague.core.world;

import com.spleefleague.core.player.CorePlayer;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public abstract class FakeWorldPlayer {

    private final CorePlayer cp;

    public FakeWorldPlayer(CorePlayer cp) {
        this.cp = cp;
    }

    public CorePlayer getCorePlayer() {
        return cp;
    }

    public Player getPlayer() {
        return cp.getPlayer();
    }

}
