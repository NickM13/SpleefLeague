/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.util.database.DBPlayer;
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
 * @author NickM13
 * @param <T>
 */
public class PlayerManager <T extends DBPlayer> implements Listener {
    
    private final Map<UUID, T> playerList;
    private final Class<T> playerClass;
    private final MongoCollection<Document> playerCol;
    
    public PlayerManager(JavaPlugin plugin, Class<T> playerClass, MongoCollection<Document> collection) {
        this.playerList = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.playerClass = playerClass;
        this.playerCol = collection;
    }
    
    public void initOnline() {
        playerList.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            load(p);
        }
        for (T p : playerList.values()) {
            p.init();
        }
    }
    
    public T get(String username) {
        return PlayerManager.this.get(Bukkit.getPlayer(username));
    }
    
    public T get(Player player) {
        if (player != null) {
            T t;
            if ((t = PlayerManager.this.get(player.getUniqueId())) != null) {
                return t;
            } else {
                load(player).init();
                return PlayerManager.this.get(player.getUniqueId());
            }
        }
        return null;
    }
    
    public T get(UUID uniqueId) {
        return playerList.containsKey(uniqueId) ? playerList.get(uniqueId) : null;
    }
    
    public T get(DBPlayer dbp) {
        return get(UUID.fromString(dbp.getUuid()));
    }
    
    public T getOffline(UUID uniqueId) {
        try {
            T p = get(uniqueId);
            if (p != null) return p;
            p = playerClass.newInstance();
            Document doc = playerCol.find(new Document("uuid", uniqueId.toString())).first();
            if (doc != null) {
                p.load(doc);
            }
            return p;
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public T getOffline(String username) {
        T p = get(username);
        if (p != null) return p;
        return getOffline(Bukkit.getOfflinePlayer(username).getUniqueId());
    }
    
    public Collection<T> getAll() {
        return playerList.values();
    }
    public Collection<T> getOnline() {
        Collection<T> col = new HashSet<>();
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            T tp = playerList.get(p.getUniqueId());
            if (!tp.isVanished()) {
                col.add(playerList.get(p.getUniqueId()));
            }
        }
        
        return col;
    }
    
    public boolean hasPlayedBefore(UUID uniqueId) {
        return playerCol.find(new Document("uuid", uniqueId.toString())).first() != null;
    }
    
    private T load(Player player) {
        if (playerList.get(player.getUniqueId()) == null) {
            try {
                T t = playerClass.getDeclaredConstructor().newInstance();
                Document pdoc = playerCol.find(new Document("uuid", player.getUniqueId().toString())).first();
                t.init(pdoc, player);
                t.setOnline(true);
                playerList.put(player.getUniqueId(), t);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return playerList.get(player.getUniqueId());
    }
    
    private void save(T player) {
        try {
            Document doc = player.save();
            if (playerCol.find(new Document("uuid", doc.get("uuid"))).first() != null) {
                playerCol.deleteMany(new Document("uuid", doc.get("uuid")));
            }
            playerCol.insertOne(doc);
        } catch (NoClassDefFoundError e) {
            System.out.println("Jar files updated, unable to save player " + player.getDisplayName());
        }
    }
    
    private void remove(Player player) {
        save(playerList.get(player.getUniqueId()));
        playerList.get(player.getUniqueId()).setOnline(false);
        playerList.remove(player.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        load(event.getPlayer()).init();
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        remove(event.getPlayer());
    }
    
    public void printPlayers() {
        for (Map.Entry<UUID, T> p : playerList.entrySet()) {
            System.out.println(p.getValue().getPlayer().getName());
        }
    }

    public void close() {
        for (T p : playerList.values()) {
            save(p);
        }
    }
    
    /*
    @Deprecated
    public void __debugRemove(Player p) {
        playerList.get(p.getUniqueId()).__debugDelete();
        playerList.remove(p.getUniqueId());
    }
    @Deprecated
    public void __debugAdd(Player p, T cp) {
        playerList.put(p.getUniqueId(), cp);
    }
    */
}
