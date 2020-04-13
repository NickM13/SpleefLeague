/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.plugin;

import com.mongodb.client.MongoDatabase;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.game.BattleManager;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.util.database.DBPlayer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author NickM13
 * @param <P>
 */
public class CorePlugin<P extends DBPlayer> extends JavaPlugin {
    
    private static final Set<CorePlugin> plugins = new HashSet<>();
    
    protected static Map<Player, Battle> playerBattlesMap = new HashMap<>();
    protected static Set<String> playerBattleNames = new HashSet<>();
    
    protected MongoDatabase pluginDb;
    
    // Map of all players in the database and their UUIDs (loaded as they connect)
    protected PlayerManager<P> playerManager;
    
    protected Map<ArenaMode, BattleManager> battleManagers = new HashMap<>();
    
    protected boolean running = false;
    
    @Override
    public void onEnable() {
        init();
        playerManager.initOnline();
        running = true;
        plugins.add(this);
    }
    public void init() {}
    
    @Override
    public void onDisable() {
        close();
        for (BattleManager bm : battleManagers.values()) {
            bm.close();
        }
        battleManagers.clear();
        plugins.remove(this);
    }
    public void close() {}
    
    public void addBattleManager(ArenaMode mode) {
        battleManagers.put(mode, BattleManager.createManager(mode));
    }
    public BattleManager getBattleManager(ArenaMode mode) {
        return battleManagers.get(mode);
    }
    
    public MongoDatabase getPluginDB() {
        return pluginDb;
    }
    
    public PlayerManager<P> getPlayers() {
        return playerManager;
    }
    
    public boolean isInBattle(Player player) {
        return getPlayers().get(player).getBattle() != null;
    }
    public Battle getPlayerBattle(Player player) {
        return getPlayers().get(player).getBattle();
    }
    
    public static boolean isInBattleGlobal(Player player) {
        for (CorePlugin plugin : plugins) {
            if (plugin.isInBattle(player)) {
                return true;
            }
        }
        return false;
    }
    
    public static void setPlayerBattle(Player player, Battle battle) {
        playerBattlesMap.put(player, battle);
        playerBattleNames.add(player.getName());
    }
    
    public static void removePlayerBattle(Player player) {
        playerBattlesMap.remove(player);
        playerBattleNames.remove(player.getName());
    }
    
    public static Set<Player> getIngamePlayers() {
        return playerBattlesMap.keySet();
    }
    public static Set<String> getIngamePlayersNames() {
        return playerBattleNames;
    }
    
    public static Set<CorePlugin> getAllPlugins() {
        return plugins;
    }
    
    public static Battle getBattleGlobal(Player player) {
        if (playerBattlesMap.containsKey(player)) {
            return playerBattlesMap.get(player);
        } else {
            return null;
        }
    }
    
    public static DBPlayer getBattlePlayerGlobal(Player player) {
        if (playerBattlesMap.containsKey(player)) {
            return playerBattlesMap.get(player).getPlugin().getPlayers().get(player);
        } else {
            return null;
        }
    }
    
    public static boolean unspectatePlayerGlobal(Player spectator) {
        if (playerBattlesMap.containsKey(spectator)) {
            Battle battle = playerBattlesMap.get(spectator);
            battle.removeSpectator(battle.getPlugin().getPlayers().get(spectator));
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean spectatePlayerGlobal(Player spectator, Player target) {
        if (playerBattlesMap.containsKey(target)) {
            Battle battle = playerBattlesMap.get(target);
            battle.addSpectator(battle.getPlugin().getPlayers().get(spectator), battle.getPlugin().getPlayers().get(target));
            return true;
        } else {
            return false;
        }
    }
    
    public void queuePlayer(ArenaMode mode, P player) {
        battleManagers.get(mode).queuePlayer(player);
    }
    public void queuePlayer(ArenaMode mode, P player, Arena arena) {
        battleManagers.get(mode).queuePlayer(player, arena);
    }
    
}
