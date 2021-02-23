package com.spleefleague.core.world;

import com.spleefleague.core.player.CorePlayer;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public abstract class FakeWorldPlayer {

    private final CorePlayer corePlayer;
    private final Player player;

    public FakeWorldPlayer(CorePlayer corePlayer) {
        this.corePlayer = corePlayer;
        this.player = corePlayer.getPlayer();
    }

    public CorePlayer getCorePlayer() {
        return corePlayer;
    }

    public Player getPlayer() {
        return player;
    }

}
