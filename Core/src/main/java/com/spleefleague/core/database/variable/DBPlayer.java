/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.database.variable;

import com.spleefleague.core.database.annotation.DBField;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Database Player
 * DBPlayers are stored in PlayerManager and link
 * to a player via UUID, used to manage stats and
 * options for each SpleefLeague plugin
 * 
 * @author NickM13
 */
public abstract class DBPlayer extends DBEntity {

    @DBField
    protected UUID uuid;
    @DBField
    protected String username;
    
    // Current battle state the player is in (None, Battler, Spectator, or Ref)
    
    protected boolean online;
    
    protected DBPlayer() {
    
    }

    /**
     * Called when a new player logs in
     *
     * @param uuid Player UUID
     * @param username Player Username
     */
    public void newPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    /**
     * Called when a player comes online
     * Initialize DBPlayer with default values
     */
    public abstract void init();
    
    public abstract void initOffline();

    /**
     * Called when player goes offline
     */
    public abstract void close();

    @Override
    public final boolean equals(Object o) {
        return (o instanceof DBPlayer
                && ((DBPlayer) o).getUniqueId().equals(this.getUniqueId()));
    }

    @Override
    public final int hashCode() {
        return getUniqueId().hashCode();
    }

    /**
     * @return Player's UUID
     */
    public final UUID getUniqueId() {
        return uuid;
    }

    /**
     * @return Player's Username
     */
    public final String getName() {
        return username;
    }

    /**
     * @return Player
     */
    public final Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * @param state Online state
     */
    public final void setOnline(boolean state) {
        online = state;
    }

    /**
     * @return Online state
     */
    public final boolean isOnline() {
        return online && getPlayer() != null;
    }

}
