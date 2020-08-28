/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.coreapi.database.variable;

import com.spleefleague.coreapi.database.annotation.DBField;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

/**
 * Database Player
 * DBPlayers are stored in PlayerManager and link
 * to a player via UUID, used to manage stats and
 * options for each SpleefLeague plugin
 * 
 * @author NickM13
 */
public abstract class DBPlayer extends DBEntity {

    protected UUID uuid;
    @DBField
    protected String username;
    protected List<Document> brokenProfiles;
    
    // Current battle state the player is in (None, Battler, Spectator, or Ref)

    public enum OnlineState {
        HERE,
        OTHER,
        OFFLINE
    }

    protected OnlineState onlineState = OnlineState.OFFLINE;
    
    protected DBPlayer() {

    }

    /**
     * Loads fields from a Document into the DBEntity
     *
     * @param doc Document
     * @return Success
     */
    @Override
    public boolean load(Document doc) {
        if (super.load(doc)) {
            return true;
        } else {
            brokenProfiles.add(doc);
            return false;
        }
    }

    /**
     * Called when a new player logs in
     *
     * @param uuid Player UUID
     * @param username Player Username
     */
    public void newPlayer(UUID uuid, String username) {
        this.identifier = uuid.toString();
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void afterLoad() {
        uuid = UUID.fromString(identifier);
    }

    public void setUsername(String username) {
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
     * @param state Online state
     */
    public void setOnline(OnlineState state) {
        onlineState = state;
    }

    public final OnlineState getOnlineState() {
        return onlineState;
    }

}
