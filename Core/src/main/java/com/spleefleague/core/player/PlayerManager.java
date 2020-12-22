/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player;

import com.mongodb.client.MongoCollection;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
 * @param <P> extends DBPlayer
 */
public class PlayerManager <P extends DBPlayer> implements Listener {

    private final Map<UUID, P> onlinePlayerList;
    private final Map<UUID, P> playerList;
    private SortedSet<P> playerListSorted;
    private final Class<P> playerClass;
    private final MongoCollection<Document> playerCol;
    private final JavaPlugin plugin;
    
    public PlayerManager(JavaPlugin plugin, Class<P> playerClass, MongoCollection<Document> collection) {
        this.onlinePlayerList = new HashMap<>();
        this.playerList = new HashMap<>();
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.playerClass = playerClass;
        this.playerCol = collection;
        playerListSorted = new TreeSet<>(Comparator.comparing(P::getName));
    }

    public void setSortingComparible(Comparator<P> comparator) {
        playerListSorted = new TreeSet<>(comparator);
        playerListSorted.addAll(playerList.values());
    }

    public Collection<P> getSortedPlayers() {
        return playerList.values();
    }

    /**
     * Called at onEnable of CorePlugin
     * Add all online players to player list
     */
    public void initOnline() {
        playerList.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            //p.kickPlayer("Sorry, reloads have to kick now :(");
            load(p.getUniqueId(), p.getName());
        }
        playerListSorted.clear();
        playerListSorted.addAll(playerList.values());
        for (P p : playerList.values()) {
            p.init();
            p.setOnline(DBPlayer.OnlineState.HERE);
            onlinePlayerList.put(p.getUniqueId(), p);
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
                p = load(player.getUniqueId(), player.getName());
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
        return playerList.get(uniqueId);
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
            Document doc = playerCol.find(new Document("identifier", uniqueId.toString())).first();
            if (doc != null) {
                p.load(doc);
                p.initOffline();
                return p;
            }
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

    public TreeSet<String> getAllNames() {
        return playerList.values().stream().map(DBPlayer::getName).collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Get all players that are not vanished
     *
     * @return Unvanished Players
     */
    public Collection<P> getOnline() {
        return onlinePlayerList.values();
    }

    /**
     * Check whether a player has logged in before by seeing
     * if they are currently in the database
     *
     * @param uniqueId Player UUID
     * @return Exists
     */
    public boolean hasPlayedBefore(UUID uniqueId) {
        return playerCol.find(new Document("identifier", uniqueId.toString())).first() != null;
    }

    /**
     * Load a DBPlayer's information in from the database
     * Called on player log in
     *
     * @param uuid Player UUID
     * @param username Player Username
     * @return DBPlayer
     */
    private P load(UUID uuid, String username) {
        if (onlinePlayerList.containsKey(uuid)) {
            if (!playerList.containsKey(uuid)) {
                playerList.put(uuid, onlinePlayerList.get(uuid));
            }
            return onlinePlayerList.get(uuid);
        }
        try {
            P p = playerClass.getDeclaredConstructor().newInstance();
            Document pdoc = playerCol.find(new Document("identifier", uuid.toString())).first();
            if (pdoc != null) {
                p.load(pdoc);
                p.setUsername(username);
            } else {
                p.newPlayer(uuid, username);
            }
            p.setOnline(DBPlayer.OnlineState.OTHER);
            playerList.put(p.getUniqueId(), p);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return playerList.get(uuid);
    }

    public void refresh(Set<UUID> players) {
        playerList.entrySet().removeIf(p -> !players.contains(p.getKey()));
        for (UUID uuid : players) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            load(uuid, op.getName());
        }
    }

    /**
     * Saves a DBPlayer's information to the database
     * Called on player log out
     *
     * @param player Player
     */
    public void save(P player) {
        try {
            if (playerCol.find(new Document("identifier", player.getUniqueId().toString())).first() != null) {
                playerCol.deleteMany(new Document("identifier", player.getUniqueId().toString()));
            }
            playerCol.insertOne(player.toDocument());

            /*
            ByteArrayDataOutput output = ByteStreams.newDataOutput();

            output.writeUTF("players");
            output.writeUTF(plugin.getName());

            output.writeInt(1);
            output.writeUTF(player.getUniqueId().toString());

            if (plugin.isEnabled()) {
                Objects.requireNonNull(Iterables.getFirst(Bukkit.getOnlinePlayers(), null)).sendPluginMessage(plugin, "slcore:refresh", output.toByteArray());
            }
            */
        } catch (NoClassDefFoundError | IllegalAccessError exception) {
            CoreLogger.logError("Jar files updated, unable to save player " + player.getName(), null);
        } catch (IllegalArgumentException exception) {
            CoreLogger.logError(null, exception);
        }
    }

    public void saveForTransfer(UUID uuid) {
        P p = get(uuid);
        if (p != null) {
            save(p);
            p.setPresaved();
        }

    }

    /**
     * Remove a DBPlayer from the player list
     * Called on player disconnects from this server
     *
     * @param player Player
     */
    private void quit(Player player) {
        if (playerList.containsKey(player.getUniqueId())) {
            P p = onlinePlayerList.remove(player.getUniqueId());
            if (p != null) {
                if (!p.isPresaved())
                    save(p);
                p.close();
            }
            playerList.get(player.getUniqueId()).setOnline(DBPlayer.OnlineState.OTHER);
        }
    }

    /**
     * When a player logs in load their DBPlayer data
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        P p = load(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        if (p != null) {
            p.setOnline(DBPlayer.OnlineState.HERE);
            p.init();
            onlinePlayerList.put(p.getUniqueId(), p);
            if (joinActions.containsKey(p.getUniqueId())) {
                for (Consumer<P> action : joinActions.get(p.getUniqueId())) {
                    action.accept(p);
                }
                joinActions.remove(p.getUniqueId());
            }
        } else {
            CoreLogger.logError(new NullPointerException());
        }
    }

    private Map<UUID, List<Consumer<P>>> joinActions = new HashMap<>();

    public void addPlayerJoinAction(UUID uuid, Consumer<P> action, boolean runIfOnline) {
        if (runIfOnline && onlinePlayerList.containsKey(uuid)) {
            action.accept(onlinePlayerList.get(uuid));
        } else {
            if (!joinActions.containsKey(uuid)) {
                joinActions.put(uuid, new ArrayList<>());
            }
            joinActions.get(uuid).add(action);
        }
    }
    
    /**
     * Test Function
     *
     * @param username Player Username
     * @return ? extends DBPlayer
     */
    public P createFakePlayer(String username) {
        Document pdoc = playerCol.find(new Document("username", username)).first();
        if (pdoc == null || playerList.containsKey(UUID.fromString(pdoc.get("identifier", String.class)))) {
            try {
                P p = playerClass.getDeclaredConstructor().newInstance();
                if (pdoc != null) {
                    p.load(pdoc);
                } else {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(username);
                    p.newPlayer(op.getUniqueId(), op.getName());
                }
                p.initOffline();
                p.setOnline(DBPlayer.OnlineState.OFFLINE);
                playerList.put(p.getUniqueId(), p);
                return p;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return playerList.get(UUID.fromString(pdoc.get("identifier", String.class)));
    }

    /**
     * When a player leaves save their DBPlayer data
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        quit(event.getPlayer());
    }

    /**
     * Save all DBPlayer
     */
    public void close() {
        for (P p : onlinePlayerList.values()) {
            save(p);
        }
    }

    public void onBungeeConnect(OfflinePlayer op) {
        load(op.getUniqueId(), op.getName());
    }

    public void onBungeeDisconnect(UUID uuid) {
        playerList.remove(uuid);
    }

}
