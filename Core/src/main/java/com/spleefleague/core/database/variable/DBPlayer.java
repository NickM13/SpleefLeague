/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.database.variable;

import com.spleefleague.core.Core;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.PregameState;
import com.spleefleague.core.plugin.CorePlugin;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
     * @param player Player
     */
    public void newPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
    }

    /**
     * Called when a player comes online
     * Initialize DBPlayer with default values
     */
    public abstract void init();

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
        return online;
    }

    /**
     * Print all saved elo stats of a player
     *
     * @deprecated Use inventory menus instead
     */
    @Deprecated
    public void printStats(DBPlayer dbp) {}

    /**
     * Get the elo of a player in a certain mode
     * Warning: Not cross-dbplayer compatible, Elos are saved
     * in plugin specific variables and database
     *
     * @param arenaMode ArenaMode
     * @return Elo
     */
    public abstract int getRating(ArenaMode arenaMode);

    /**
     * Change the elo of a player in a certain mode
     * Warning: Not cross-dbplayer compatible, Elos are saved
     * in plugin specific variables and database
     *
     * @param arenaMode ArenaMode
     * @param rating Elo Change
     */
    public abstract void addRating(ArenaMode arenaMode, int rating);

    /**
     * Get a formatted String of the elo of a player in a certain mode
     * Warning: Not cross-dbplayer compatible, Elos are saved
     * in plugin specific variables and database
     *
     * @param arenaMode ArenaMode
     * @return Elo as a formatted String
     */
    public abstract String getDisplayElo(ArenaMode arenaMode);

}
