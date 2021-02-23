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

import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Manager for custom DBPlayer objects based on Player UUIDs
 *
 * @param <ONLINE> extends DBPlayer
 * @author NickM13
 */
public class PlayerManager<ONLINE extends OFFLINE, OFFLINE extends CoreDBPlayer> {

    // Players on this server
    protected final Map<UUID, ONLINE> localPlayerMap;
    // Players on any server
    protected final Map<UUID, ONLINE> onlinePlayerMap;
    // Offline players to prevent loading offline players more than once
    protected final Map<UUID, OFFLINE> offlinePlayerList;
    protected SortedSet<ONLINE> playerListSorted;
    protected final Class<ONLINE> onlinePlayerClass;
    protected final Class<OFFLINE> offlinePlayerClass;
    protected final MongoCollection<Document> playerCol;
    protected boolean saving = false;

    public PlayerManager(Class<ONLINE> onlineClass, Class<OFFLINE> offlineClass, MongoCollection<Document> collection) {
        this.localPlayerMap = new HashMap<>();
        this.onlinePlayerMap = new HashMap<>();
        this.offlinePlayerList = new HashMap<>();
        this.onlinePlayerClass = onlineClass;
        this.offlinePlayerClass = offlineClass;
        this.playerCol = collection;
        this.playerListSorted = new TreeSet<>(Comparator.comparing(ONLINE::getName));
        CorePlugin.registerPlayerManager(this);
    }

    public Class<ONLINE> getOnlinePlayerClass() {
        return onlinePlayerClass;
    }

    public void enableSaving() {
        saving = true;
    }

    public void setSortingComparible(Comparator<ONLINE> comparator) {
        playerListSorted = new TreeSet<>(comparator);
        playerListSorted.addAll(onlinePlayerMap.values());
    }

    public Collection<ONLINE> getSortedPlayers() {
        return onlinePlayerMap.values();
    }

    /**
     * Called at onEnable of CorePlugin
     * Add all online players to player list
     */
    public void initOnline() {
        onlinePlayerMap.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            load(p.getUniqueId(), p.getName());
        }
        playerListSorted.clear();
        playerListSorted.addAll(onlinePlayerMap.values());
        for (ONLINE p : onlinePlayerMap.values()) {
            Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
                p.init();
                p.setOnline(DBPlayer.OnlineState.HERE);
                localPlayerMap.put(p.getUniqueId(), p);
            });
        }
    }

    /**
     * Get the DBPlayer by Player Username
     *
     * @param username Player Username
     * @return DBPlayer
     */
    public ONLINE get(String username) {
        return PlayerManager.this.get(Bukkit.getPlayer(username));
    }

    /**
     * Get the DBPlayer by Player
     *
     * @param player Player
     * @return DBPlayer
     */
    public ONLINE get(Player player) {
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
    public ONLINE get(UUID uniqueId) {
        return onlinePlayerMap.get(uniqueId);
    }

    public ONLINE getLocal(UUID uniqueId) {
        return localPlayerMap.get(uniqueId);
    }

    /**
     * Returns the DBPlayer related to passed DBPlayer
     *
     * @param dbp DBPlayer
     * @return DBPlayer
     */
    public ONLINE get(DBPlayer dbp) {
        return get(dbp.getUniqueId());
    }

    /**
     * Get the DBPlayer of an offline player by UUID
     *
     * @param uniqueId Player UUID
     * @return DBPlayer
     */
    public OFFLINE getOffline(UUID uniqueId) {
        try {
            ONLINE p;
            if ((p = get(uniqueId)) != null) return p;
            OFFLINE op = offlinePlayerList.get(uniqueId);
            if (op != null/* && op.getLastOfflineLoad() > System.currentTimeMillis() - 1000*/) {
                return op;
            }
            op = offlinePlayerClass.getDeclaredConstructor().newInstance();
            Document doc = playerCol.find(new Document("identifier", uniqueId.toString())).first();
            if (doc != null) {
                op.load(doc);
                op.init();
                offlinePlayerList.put(uniqueId, op);
                return op;
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
    public OFFLINE getOffline(String username) {
        Player p = Bukkit.getPlayer(username);
        if (p != null) return getOffline(p.getUniqueId());
        return getOffline(Bukkit.getOfflinePlayer(username).getUniqueId());
    }

    public boolean isOnline(UUID uuid) {
        return onlinePlayerMap.containsKey(uuid);
    }

    public boolean isLocal(UUID uuid) {
        ONLINE p = localPlayerMap.get(uuid);
        return p != null && p.getOnlineState() != DBPlayer.OnlineState.OFFLINE;
    }

    /**
     * Get all players, including vanished
     *
     * @return Players
     */
    public Collection<ONLINE> getAll() {
        return onlinePlayerMap.values();
    }

    /**
     * Get list of all player names, non-vanished
     *
     * @return Sorted Name List
     */
    public TreeSet<String> getAllNames() {
        return onlinePlayerMap.values().stream().map(DBPlayer::getName).collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Get all players on this server, including vanished
     *
     * @return All players on this server
     */
    public Collection<ONLINE> getAllLocal() {
        return localPlayerMap.values();
    }

    /**
     * Get all players on bungee, including vanished
     *
     * @return Bungee players, including vanished
     */
    public Collection<ONLINE> getAllOnline() {
        return onlinePlayerMap.values();
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
     * @param uuid     Player UUID
     * @param username Player Username
     * @return DBPlayer
     */
    protected ONLINE load(UUID uuid, String username) {
        offlinePlayerList.remove(uuid);
        if (localPlayerMap.containsKey(uuid)) {
            ONLINE p = localPlayerMap.get(uuid);
            onlinePlayerMap.put(uuid, p);
            return p;
        }
        try {
            Document pdoc = playerCol.find(new Document("identifier", uuid.toString())).first();
            ONLINE online = onlinePlayerClass.getDeclaredConstructor().newInstance();
            if (pdoc != null) {
                online.load(pdoc);
                online.setUsername(username);
            } else {
                online.newPlayer(uuid, username);
            }
            online.setOnline(DBPlayer.OnlineState.OTHER);
            onlinePlayerMap.put(uuid, online);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return onlinePlayerMap.get(uuid);
    }

    public void resync(UUID uuid, List<PacketBungeePlayerResync.Field> fields) {
        if (!onlinePlayerMap.containsKey(uuid)) {
            CoreLogger.logWarning("Attempted to reload field of offline player");
        } else {
            onlinePlayerMap.get(uuid).reloadField(
                    playerCol.find(new Document("identifier", uuid.toString())).first(),
                    fields.stream().map(PacketBungeePlayerResync.Field::getFieldName).collect(Collectors.toSet()));
            onlinePlayerMap.get(uuid).onResync(fields);
        }
    }

    public void refresh(Set<UUID> players) {
        players.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));
        onlinePlayerMap.entrySet().removeIf(p -> !players.contains(p.getKey()));
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
     */
    protected void quit(UUID uuid) {
        ONLINE p = localPlayerMap.remove(uuid);
        if (p != null) {
            p.setOnline(DBPlayer.OnlineState.OTHER);
            p.close();
            if (saving) {
                p.save(playerCol);
            }
        } else {
            if (onlinePlayerMap.containsKey(uuid)) {
                onlinePlayerMap.get(uuid).setOnline(DBPlayer.OnlineState.OTHER);
            }
        }
    }

    /**
     * When a player logs in load their DBPlayer data
     */
    public void onPlayerJoin(Player player) {
        ONLINE p = load(player.getUniqueId(), player.getName());
        if (p != null) {
            p.setPlayer(player);
            p.init();
            p.setOnline(DBPlayer.OnlineState.HERE);
            localPlayerMap.put(p.getUniqueId(), p);

            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                if (joinActions.containsKey(p.getUniqueId())) {
                    for (Consumer<ONLINE> action : joinActions.get(p.getUniqueId())) {
                        action.accept(p);
                    }
                    joinActions.remove(p.getUniqueId());
                }
            }, 10L);
        } else {
            CoreLogger.logError(new NullPointerException());
        }
    }

    protected Map<UUID, List<Consumer<ONLINE>>> joinActions = new HashMap<>();

    public void addPlayerJoinAction(UUID uuid, Consumer<ONLINE> action, boolean runIfOnline) {
        if (runIfOnline && localPlayerMap.containsKey(uuid)) {
            action.accept(localPlayerMap.get(uuid));
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
    public ONLINE createFakePlayer(String username) {
        Document pdoc = playerCol.find(new Document("username", username)).first();
        if (pdoc == null || onlinePlayerMap.containsKey(UUID.fromString(pdoc.get("identifier", String.class)))) {
            try {
                ONLINE p = onlinePlayerClass.getDeclaredConstructor().newInstance();
                if (pdoc != null) {
                    p.load(pdoc);
                } else {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(username);
                    p.newPlayer(op.getUniqueId(), op.getName());
                }
                p.setOnline(DBPlayer.OnlineState.OFFLINE);
                p.initOffline();
                onlinePlayerMap.put(p.getUniqueId(), p);
                return p;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return onlinePlayerMap.get(UUID.fromString(pdoc.get("identifier", String.class)));
    }

    /**
     * Called when a player disconnects from the server
     */
    public void onPlayerQuit(UUID uuid) {
        quit(uuid);
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
        onlinePlayerMap.remove(uuid);
    }

}
