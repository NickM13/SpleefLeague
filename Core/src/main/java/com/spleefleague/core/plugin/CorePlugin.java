/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.plugin;

import com.google.common.collect.Sets;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.game.manager.BattleManager;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.database.variable.DBPlayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.spleefleague.core.world.build.BuildWorld;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * CorePlugin is the base class for all SpleefLeague plugins,
 * all plugins that use this are stored in a plugins master list
 * allowing for synced battle states
 *
 * @author NickM13
 * @param <P>
 */
public abstract class CorePlugin<P extends DBPlayer> extends JavaPlugin {
    
    private static final Set<CorePlugin<?>> plugins = new HashSet<>();
    
    // For quick referencing in tab command auto completes
    protected static Set<String> ingamePlayerNames = new HashSet<>();
    
    private static MongoClient mongoClient;
    private MongoDatabase pluginDb;
    
    // Map of all players in the database and their UUIDs (loaded as they connect)
    protected PlayerManager<P> playerManager;
    
    protected Map<ArenaMode, BattleManager<?>> battleManagers = new HashMap<>();
    
    protected boolean running = false;

    /**
     * Initialize plugin and online players (for /reloads)
     *
     * Don't override this!  Use init()
     */
    @Override
    public final void onEnable() {
        init();
        playerManager.initOnline();
        running = true;
        plugins.add(this);
    }
    protected abstract void init();

    /**
     * Terminates BattleManagers
     *
     * Don't override this!  Use close()
     */
    @Override
    public final void onDisable() {
        close();
        for (BattleManager<?> bm : battleManagers.values()) {
            bm.close();
        }
        battleManagers.clear();
        plugins.remove(this);
    }
    protected abstract void close();
    
    public final void addBattleManager(ArenaMode mode) {
        battleManagers.put(mode, BattleManager.createManager(mode));
    }
    public final BattleManager<?> getBattleManager(ArenaMode mode) {
        return battleManagers.get(mode);
    }

    public final void queuePlayer(ArenaMode mode, CorePlayer cp) {
        battleManagers.get(mode).queuePlayer(cp);
    }
    public final void queuePlayer(ArenaMode mode, CorePlayer cp, Arena arena) {
        battleManagers.get(mode).queuePlayer(cp, arena);
    }
    
    /**
     * Sets the main database used by this plugin
     * mongoClient must be initialized before calling
     *
     * @param databaseName Database Name
     */
    protected final void setPluginDB(String databaseName) {
        pluginDb = mongoClient.getDatabase(databaseName);
    }
    
    /**
     * Gets the main database used by this plugin
     *
     * @return Mongo Database
     */
    public final MongoDatabase getPluginDB() {
        return pluginDb;
    }
    
    /**
     * Player manager that contains all online players for this plugin
     *
     * @return Player Manager
     */
    public final PlayerManager<P> getPlayers() {
        return playerManager;
    }
    
    /**
     * Adds a player to the quick ingame player name
     * references list
     *
     * @param cp Core Player
     */
    public static void addIngamePlayerName(CorePlayer cp) {
        ingamePlayerNames.add(cp.getName());
    }
    
    /**
     * Removes a player name from the quick ingame player name
     * refrences  list
     *
     * @param cp Core Player
     */
    public static void removeIngamePlayerName(CorePlayer cp) {
        ingamePlayerNames.remove(cp.getName());
    }
    
    /**
     * Returns a set of the names of all ingame players
     * for quick autocompletion
     *
     * @return Ingame Names Set
     */
    public static Set<String> getIngamePlayerNames() {
        return ingamePlayerNames;
    }
    
    /**
     * Returns a set of all plugins that use CorePlugin and
     * are enabled
     *
     * @return CorePlugins Set
     */
    public static Set<CorePlugin<?>> getAllPlugins() {
        return plugins;
    }
    
    /**
     * Remove a spectator from a battle
     *
     * @param spectator Spectator DBPlayer
     */
    public static void unspectatePlayer(CorePlayer spectator) {
        if (spectator.getBattleState().equals(BattleState.SPECTATOR)) {
            Battle<?> battle = spectator.getBattle();
            battle.removeSpectator(spectator);
        }
    }
    
    /**
     * Find a player's battle and put the spectator into it, fails
     * if the target player isn't in a battle
     *
     * @param spectator Spectator DBPlayer
     * @param target Target DBPlayer
     * @return Success
     */
    public static boolean spectatePlayerGlobal(CorePlayer spectator, CorePlayer target) {
        if (!spectator.isInBattle() && target.isInBattle()) {
            target.getBattle().addSpectator(spectator, target);
            return true;
        }
        return false;
    }
    
    /**
     * Connect to the Mongo database based on the mongo.cfg file
     * that should be in the server's folder
     */
    public static void initMongo() {
        try {
            Properties mongoProps = new Properties();
            String mongoPath = System.getProperty("user.dir") + "\\mongo.cfg";
            FileInputStream file = new FileInputStream(mongoPath);
            mongoProps.load(file);
            file.close();
            
            String mongoPrefix = mongoProps.getProperty("prefix");
            String credentials = mongoProps.getProperty("credentials", "");
            if (!credentials.isEmpty()) credentials = credentials.concat("@");
            String host = mongoProps.getProperty("host", "localhost:27017") + "/";
            String defaultauthdb = mongoProps.getProperty("defaultauthdb", "admin") + "?";
            String options = mongoProps.getProperty("options", "");
            MongoClientURI uri = new MongoClientURI(mongoPrefix + credentials + host + defaultauthdb + options);
            mongoClient = new MongoClient(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Disconnect the Mongo database, catches an error
     * if a jar file was forcibly changed while in use
     * through the windows xcopy command (test reasons)
     */
    public static void closeMongo() {
        try {
            mongoClient.close();
        } catch (NoClassDefFoundError e) {
            System.out.println("Jar files updated, unable to close MongoDB");
        }
    }
    
}
