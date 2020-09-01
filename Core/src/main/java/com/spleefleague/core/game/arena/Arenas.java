package com.spleefleague.core.game.arena;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.logger.CoreLogger;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * @author NickM13
 * @since 5/1/2020
 */
public class Arenas {
    
    private static final SortedMap<String, Arena> arenaMap = new TreeMap<>();
    private static final Map<String, SortedMap<String, Arena>> modeArenaMap = new HashMap<>();
    private static MongoCollection<Document> arenaCol;
    private static final Set<Arena> globalArenas = new HashSet<>();
    
    public static void init() {
        arenaMap.clear();
        modeArenaMap.clear();
        globalArenas.clear();
        
        arenaCol = Core.getInstance().getPluginDB().getCollection("Arenas");
        MongoCursor<Document> arenaDocs = arenaCol.find().cursor();
        Map<String, Integer> successMap = new HashMap<>();
        while (arenaDocs.hasNext()) {
            Document arenaDoc = arenaDocs.next();
            Arena arena = new Arena();
            if (arena.load(arenaDoc)) {
                for (String modeName : arena.getModes()) {
                    if (!successMap.containsKey(modeName)) {
                        successMap.put(modeName, 0);
                    }
                    successMap.put(modeName, successMap.get(modeName) + 1);
                    if (!modeArenaMap.containsKey(modeName)) {
                        modeArenaMap.put(modeName, new TreeMap<>());
                    }
                    modeArenaMap.get(modeName).put(arena.getIdentifier(), arena);
                }
                arenaMap.put(arena.getIdentifier(), arena);
            }
        }
        for (Map.Entry<String, Integer> entry : successMap.entrySet()) {
            CoreLogger.logInfo("Arenas for " + entry.getKey() + ": " + entry.getValue());
        }
    }
    
    public static void saveArenaDB(Arena arena) {
        arena.save(arenaCol);
    }

    public static void unsaveArenaDB(Arena arena) {
        arena.unsave(arenaCol);
    }
    
    public static Arena createArena(String identifier, String arenaName) {
        identifier = ChatColor.stripColor(identifier.replaceAll("\\s", "").toLowerCase());
        if (!arenaMap.containsKey(identifier)) {
            Arena arena = new Arena(identifier, arenaName);
            arenaMap.put(arena.getIdentifier(), arena);
            saveArenaDB(arena);
            return arena;
        }
        return null;
    }

    public static Arena cloneArena(String fromIdentifier, String toIdentifier) {
        toIdentifier = toIdentifier.toLowerCase();
        if (arenaMap.keySet().contains(toIdentifier)) {
            return null;
        }
        Arena arena = get(fromIdentifier);
        Arena newArena = createArena(toIdentifier, arena.getName());
        newArena.cloneFrom(arena);
        return newArena;
    }

    public static boolean destroyArena(String identifier) {
        if (arenaMap.containsKey(identifier)) {
            Arena arena = arenaMap.remove(identifier);
            unsaveArenaDB(arena);
            for (String mode : arena.getModes()) {
                modeArenaMap.get(mode).remove(identifier);
            }
            return true;
        }
        return false;
    }
    
    public static boolean renameArena(String identifier, String arenaName) {
        Arena arena = arenaMap.get(identifier);
        if (arena != null) {
            arena.setName(arenaName);
            saveArenaDB(arena);
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean addArenaMode(String arenaName, BattleMode mode) {
        Arena arena = arenaMap.get(arenaName);
        if (arena != null && arena.addMode(mode.getName())) {
            if (!modeArenaMap.containsKey(mode.getName())) {
                modeArenaMap.put(mode.getName(), new TreeMap<>());
            }
            modeArenaMap.get(mode.getName()).put(arena.getIdentifier(), arena);
            saveArenaDB(arena);
            return true;
        }
        return false;
    }
    
    public static boolean removeArenaMode(String arenaName, BattleMode mode) {
        Arena arena = arenaMap.get(arenaName);
        if (arena != null && arena.removeMode(mode.getName())) {
            modeArenaMap.get(mode.getName()).remove(arenaName);
            saveArenaDB(arena);
            return true;
        }
        return false;
    }
    
    public static Set<Arena> getGlobal() {
        return globalArenas;
    }
    
    /**
     * Get all arenas for a specific mode that are available
     *
     * @param mode Battle Mode
     * @return Available Arenas
     */
    public static Map<String, Arena> getUnpaused(BattleMode mode) {
        if (!modeArenaMap.containsKey(mode.getName())) {
            return new HashMap<>();
        }
        Map<String, Arena> arenas = new TreeMap<>();
        
        for (Map.Entry<String, Arena> entry : modeArenaMap.get(mode.getName()).entrySet()) {
            if (entry.getValue().isAvailable()) {
                arenas.put(entry.getKey(), entry.getValue());
            }
        }
        
        return arenas;
    }
    
    /**
     * Get all arenas for a specific mode
     *
     * @param mode Battle Mode
     * @return Arenas
     */
    public static SortedMap<String, Arena> getAll(BattleMode mode) {
        if (!modeArenaMap.containsKey(mode.getName())) {
            return new TreeMap<>();
        }
        return modeArenaMap.get(mode.getName());
    }
    
    public static SortedMap<String, Arena> getAll() {
        return arenaMap;
    }
    
    /**
     * Returns a random arena of a mode
     *
     * @param mode Battle Mode
     * @return Arena
     */
    public static Arena getRandom(BattleMode mode) {
        Map<String, Arena> arenas = getUnpaused(mode);
        if (arenas.isEmpty()) return null;
        return (Arena)(arenas.values().toArray()[new Random().nextInt(arenas.values().size())]);
    }
    
    /**
     * Returns an arena by name and mode
     *
     * @param arenaName Arena Name
     * @param mode Battle Mode
     * @return Arena
     */
    public static Arena get(String arenaName, BattleMode mode) {
        if (arenaName == null) return null;
        Map<String, Arena> arenas = modeArenaMap.get(mode.getName());
        if (arenas != null) {
            if (arenaName.equals("")) {
                return getRandom(mode);
            } else {
                return arenas.get(arenaName);
            }
        }
        return null;
    }
    
    public static Arena get(String name) {
        return arenaMap.get(name);
    }
    
}