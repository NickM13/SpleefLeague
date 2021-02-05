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

import com.mongodb.client.model.ReplaceOptions;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manager for custom DBPlayer objects based on Player UUIDs
 *
 * @author NickM13
 * @param <P> extends DBPlayer
 */
public class PlayerManager <P extends DBPlayer> implements Listener {

    // Players on this server (non-vanished)
    protected final Map<UUID, P> herePlayerList;
    // Players on this server
    protected final Map<UUID, P> herePlayerListAll;
    // Players on any server (non-vanished)
    protected final Map<UUID, P> onlinePlayerList;
    // Players on any server
    protected final Map<UUID, P> onlinePlayerListAll;
    // Offline players to prevent loading offline players more than once
    protected final Map<UUID, P> offlinePlayerList;
    protected SortedSet<P> playerListSorted;
    protected final Class<P> playerClass;
    protected final MongoCollection<Document> playerCol;
    protected final JavaPlugin plugin;

    public PlayerManager(JavaPlugin plugin, Class<P> playerClass, MongoCollection<Document> collection) {
        this.herePlayerList = new HashMap<>();
        this.herePlayerListAll = new HashMap<>();
        this.onlinePlayerList = new HashMap<>();
        this.onlinePlayerListAll = new HashMap<>();
        this.offlinePlayerList = new HashMap<>();
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.playerClass = playerClass;
        this.playerCol = collection;
        this.playerListSorted = new TreeSet<>(Comparator.comparing(P::getName));
    }

    public void setSortingComparible(Comparator<P> comparator) {
        playerListSorted = new TreeSet<>(comparator);
        playerListSorted.addAll(onlinePlayerListAll.values());
    }

    public Collection<P> getSortedPlayers() {
        return onlinePlayerListAll.values();
    }

    /**
     * Called at onEnable of CorePlugin
     * Add all online players to player list
     */
    public void initOnline() {
        onlinePlayerListAll.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            load(p.getUniqueId(), p.getName());
        }
        playerListSorted.clear();
        playerListSorted.addAll(onlinePlayerListAll.values());
        for (P p : onlinePlayerListAll.values()) {
            p.init();
            p.setOnline(DBPlayer.OnlineState.HERE);
            if (!Core.getInstance().getPlayers().get(p).isVanished()) {
                herePlayerList.put(p.getUniqueId(), p);
            }
            herePlayerListAll.put(p.getUniqueId(), p);
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
            return get(player.getUniqueId());
            /*
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
             */
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
        return onlinePlayerListAll.get(uniqueId);
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
            P p;
            if ((p = get(uniqueId)) != null) return p;
            p = offlinePlayerList.get(uniqueId);
            if (p != null/* && p.getLastOfflineLoad() > System.currentTimeMillis() - 1000*/) return p;
            p = playerClass.getDeclaredConstructor().newInstance();
            Document doc = playerCol.find(new Document("identifier", uniqueId.toString())).first();
            if (doc != null) {
                p.load(doc);
                p.initOffline();
                offlinePlayerList.put(uniqueId, p);
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
        Player p = Bukkit.getPlayer(username);
        if (p != null) return getOffline(p.getUniqueId());
        return getOffline(Bukkit.getOfflinePlayer(username).getUniqueId());
    }

    public boolean isOnline(UUID uuid) {
        return onlinePlayerList.containsKey(uuid);
    }

    /**
     * Get all players, including vanished
     *
     * @return Players
     */
    public Collection<P> getAll() {
        return onlinePlayerListAll.values();
    }

    /**
     * Get list of all player names, non-vanished
     *
     * @return
     */
    public TreeSet<String> getAllNames() {
        return onlinePlayerList.values().stream().map(DBPlayer::getName).collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Get all players that are not vanished
     *
     * @return Unvanished Players
     */
    public Collection<P> getAllHere() {
        return herePlayerList.values();
    }

    /**
     * Get all players on this server, including vanished
     *
     * @return All players on this server
     */
    public Collection<P> getAllHereExtended() {
        return herePlayerListAll.values();
    }

    /**
     * Get all players on bungee, excluding vanished
     *
     * @return Bungee players, excluding vanished
     */
    public Collection<P> getAllOnline() {
        return onlinePlayerList.values();
    }

    /**
     * Get all players on bungee, including vanished
     *
     * @return Bungee players, including vanished
     */
    public Collection<P> getAllOnlineExtended() {
        return onlinePlayerListAll.values();
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
    protected P load(UUID uuid, String username) {
        offlinePlayerList.remove(uuid);
        if (herePlayerListAll.containsKey(uuid)) {
            P p = herePlayerListAll.get(uuid);
            if (!onlinePlayerListAll.containsKey(uuid)) {
                onlinePlayerListAll.put(uuid, p);
                // Include vanished code here
                if (plugin == Core.getInstance()) {
                    CorePlayer cp = (CorePlayer) p;
                    if (!cp.isVanished()) {
                        onlinePlayerList.put(uuid, p);
                        herePlayerList.put(uuid, p);
                    }
                } else {
                    onlinePlayerList.put(uuid, p);
                    herePlayerList.put(uuid, p);
                }
            }
            return herePlayerListAll.get(uuid);
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
            onlinePlayerListAll.put(uuid, p);

            if (plugin == Core.getInstance()) {
                CorePlayer cp = (CorePlayer) p;
                if (!cp.isVanished()) {
                    onlinePlayerList.put(uuid, p);
                }
            } else {
                onlinePlayerList.put(uuid, p);
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return onlinePlayerListAll.get(uuid);
    }

    public void resync(UUID uuid, List<PacketBungeePlayerResync.Field> fields) {
        if (!onlinePlayerListAll.containsKey(uuid)) {
            CoreLogger.logWarning("Attempted to reload field of offline player");
            return;
        }
    }

    public void refresh(Set<UUID> players) {
        onlinePlayerListAll.entrySet().removeIf(p -> !players.contains(p.getKey()));
        onlinePlayerList.entrySet().removeIf(p -> !players.contains(p.getKey()));
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
    /*
    public void save(P player) {
        try {
            playerCol.replaceOne(new Document("identifier", player.getUniqueId().toString()), player.toDocument(), new ReplaceOptions().upsert(true));
        } catch (NoClassDefFoundError | IllegalAccessError exception) {
            CoreLogger.logError("Jar files updated, unable to save player " + player.getName(), null);
        } catch (IllegalArgumentException exception) {
            CoreLogger.logError(null, exception);
        }
    }

    public void saveForTransfer(UUID uuid) {
        P p = herePlayerListAll.get(uuid);
        if (p != null) {
            save(p);
            p.setPresaved();
        }
    }
    */

    /**
     * Remove a DBPlayer from the player list
     * Called on player disconnects from this server
     *
     * @param player Player
     */
    protected void quit(Player player) {
        P p = herePlayerListAll.remove(player.getUniqueId());
        herePlayerList.remove(player.getUniqueId());
        if (p != null) {
            /*
            if (!p.isPresaved()) {
                save(p);
            }
            */
            p.close();
            p.setOnline(DBPlayer.OnlineState.OTHER);
        } else {
            if (onlinePlayerListAll.containsKey(player.getUniqueId())) {
                onlinePlayerListAll.get(player.getUniqueId()).setOnline(DBPlayer.OnlineState.OTHER);
            }
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
            herePlayerListAll.put(p.getUniqueId(), p);

            if (plugin == Core.getInstance()) {
                CorePlayer cp = (CorePlayer) p;
                if (!cp.isVanished()) {
                    herePlayerList.put(p.getUniqueId(), p);
                }
            } else {
                herePlayerList.put(p.getUniqueId(), p);
            }
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

    protected Map<UUID, List<Consumer<P>>> joinActions = new HashMap<>();

    public void addPlayerJoinAction(UUID uuid, Consumer<P> action, boolean runIfOnline) {
        if (runIfOnline && herePlayerListAll.containsKey(uuid)) {
            action.accept(herePlayerListAll.get(uuid));
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
        if (pdoc == null || onlinePlayerListAll.containsKey(UUID.fromString(pdoc.get("identifier", String.class)))) {
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
                onlinePlayerListAll.put(p.getUniqueId(), p);
                return p;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return onlinePlayerListAll.get(UUID.fromString(pdoc.get("identifier", String.class)));
    }

    /**
     * Called when a player disconnects from the server
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

    }

    public void onBungeeConnect(OfflinePlayer op) {
        load(op.getUniqueId(), op.getName());
    }

    public void onBungeeDisconnect(UUID uuid) {
        onlinePlayerListAll.remove(uuid);
        onlinePlayerList.remove(uuid);
    }

}
