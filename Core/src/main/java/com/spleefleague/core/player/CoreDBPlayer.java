package com.spleefleague.core.player;

import com.spleefleague.core.Core;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

/**
 * @author NickM13
 * @since 2/19/2021
 */
public abstract class CoreDBPlayer extends DBPlayer {

    protected Player player;

    public void setPlayer(Player player) {
        this.player = player;
        this.identifier = player.getUniqueId().toString();
        this.uuid = player.getUniqueId();
        this.username = player.getName();
    }

    public Player getPlayer() {
        return player;
    }

    public void preInit() {

    }

    public boolean onResync(List<PacketBungeePlayerResync.Field> fields) {
        return false;
    }

}
