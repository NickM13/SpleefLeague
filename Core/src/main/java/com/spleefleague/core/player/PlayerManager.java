/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.database.variable.DBPlayer;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manager for custom DBPlayer objects based on Player UUIDs
 *
 * @author NickM13
 * @param <P>
 */
public class PlayerManager <P extends DBPlayer> implements Listener {
    
    private final Map<UUID, P> playerList;
    private final Class<P> playerClass;
    private final MongoCollection<Document> playerCol;
    
    public PlayerManager(JavaPlugin plugin, Class<P> playerClass, MongoCollection<Document> collection) {
        this.playerList = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.playerClass = playerClass;
        this.playerCol = collection;
    }

    /**
     * Called on startup of CorePlugin
     * Add all online players to player list
     */
    public void initOnline() {
        playerList.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            load(p);
        }
        for (P p : playerList.values()) {
            p.init();
        }
    }

    /**
     * Get the DBPlayer by Player Username
     *
     * @param username Player Username
     * @return DBPlayer
     */
    public P get(String username) {
        return PlayerManager.this.get(Bukkit.getPlayer(username));
    }

    /**
     * Get the DBPlayer by Player
     *
     * @param player Player
     * @return DBPlayer
     */
    public P get(Player player) {
        if (player != null) {
            P p;
            if ((p = PlayerManager.this.get(player.getUniqueId())) != null) {
                return p;
            } else {
                p = load(player);
                if (p != null) {
                    p.init();
                    return PlayerManager.this.get(player.getUniqueId());
                }
            }
        }
        return null;
    }

    /**
     * Get the DBPlayer by UUID
     *
     * @param uniqueId Player UUID
     * @return DBPlayer
     */
    public P get(UUID uniqueId) {
        return playerList.getOrDefault(uniqueId, null);
    }

    /**
     * Returns the DBPlayer related to passed DBPlayer
     *
     * @param dbp DBPlayer
     * @return DBPlayer
     */
    public P get(DBPlayer dbp) {
        return get(dbp.getUniqueId());
    }

    /**
     * Get the DBPlayer of an offline player by UUID
     *
     * @param uniqueId Player UUID
     * @return DBPlayer
     */
    public P getOffline(UUID uniqueId) {
        try {
            P p = get(uniqueId);
            if (p != null) return p;
            p = playerClass.getDeclaredConstructor().newInstance();
            Document doc = playerCol.find(new Document("uuid", uniqueId.toString())).first();
            if (doc != null) {
                p.load(doc);
            }
            return p;
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException ex) {
            Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Get the DBPlayer of an offline player by name
     *
     * @param username Username
     * @return DBPlayer
     * @deprecated Bukkit's getOfflinePlayer(name) is deprecated
     */
    @Deprecated
    public P getOffline(String username) {
        P p = get(username);
        if (p != null) return p;
        return getOffline(Bukkit.getOfflinePlayer(username).getUniqueId());
    }

    /**
     * Get all players, including vanished
     *
     * @return Players
     */
    public Collection<P> getAll() {
        return playerList.values();
    }

    /**
     * Get all players that are not vanished
     *
     * @return Unvanished Players
     */
    public Collection<P> getOnline() {
        Collection<P> col = new HashSet<>();
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!Core.getInstance().getPlayers().get(p).isVanished()) {
                col.add(playerList.get(p.getUniqueId()));
            }
        }
        
        return col;
    }

    /**
     * Check whether a player has logged in before by seeing
     * if they are currently in the database
     *
     * @param uniqueId Player UUID
     * @return Exists
     */
    public boolean hasPlayedBefore(UUID uniqueId) {
        return playerCol.find(new Document("uuid", uniqueId.toString())).first() != null;
    }

    /**
     * Load a DBPlayer's information in from the database
     * Called on player log in
     *
     * @param player Player
     * @return DBPlayer
     */
    private P load(Player player) {
        if (playerList.get(player.getUniqueId()) == null) {
            try {
                P p = playerClass.getDeclaredConstructor().newInstance();
                Document pdoc = playerCol.find(new Document("uuid", player.getUniqueId().toString())).first();
                if (pdoc != null) {
                    p.load(pdoc);
                } else {
                    p.newPlayer(player);
                }
                p.setOnline(true);
                playerList.put(p.getUniqueId(), p);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return playerList.get(player.getUniqueId());
    }

    /**
     * Saves a DBPlayer's information to the database
     * Called on player log out
     *
     * @param player Player
     */
    private void save(P player) {
        try {
            Document doc = player.save();
            if (playerCol.find(new Document("uuid", doc.get("uuid"))).first() != null) {
                playerCol.deleteMany(new Document("uuid", doc.get("uuid")));
            }
            playerCol.insertOne(doc);
        } catch (NoClassDefFoundError e) {
            System.out.println("Jar files updated, unable to save player " + player.getName());
        }
    }

    /**
     * Remove a DBPlayer from the player list
     * Called on player log out
     *
     * @param player Player
     */
    private void remove(Player player) {
        save(playerList.get(player.getUniqueId()));
        playerList.get(player.getUniqueId()).setOnline(false);
        playerList.remove(player.getUniqueId());
    }

    /**
     * When a player logs in load their DBPlayer data
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        P p = load(event.getPlayer());
        if (p != null) p.init();
    }

    /**
     * When a player leaves save their DBPlayer data
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        remove(event.getPlayer());
    }

    /**
     * Save all DBPlayer
     */
    public void close() {
        for (P p : playerList.values()) {
            save(p);
        }
    }

}
