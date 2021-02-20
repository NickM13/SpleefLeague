package com.spleefleague.core.player;

import com.spleefleague.coreapi.database.variable.DBPlayer;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 * @since 2/19/2021
 */
public abstract class CoreDBPlayer extends DBPlayer {

    protected Player player;

    public void init(Player player) {
        this.player = player;
        init();
    }

    public Player getPlayer() {
        return player;
    }

}
