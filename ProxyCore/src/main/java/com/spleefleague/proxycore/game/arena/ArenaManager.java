package com.spleefleague.proxycore.game.arena;

import com.spleefleague.proxycore.ProxyCore;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author NickM13
 * @since 6/25/2020
 */
public class ArenaManager {

    private static final Map<String, SortedMap<String, Arena>> modeArenaMap = new HashMap<>();
    private static final SortedMap<String, Arena> arenaMap = new TreeMap<>();

    public static void init() {
        loadArenas();
    }

    public static void loadArenas() {
        for (Document doc : ProxyCore.getInstance().getDatabase().getCollection("Arenas").find()) {
            Arena arena = new Arena();
            arena.load(doc);
            arenaMap.put(arena.getIdentifier(), arena);
            for (String mode : arena.getModes()) {
                if (!modeArenaMap.containsKey(mode)) {
                    modeArenaMap.put(mode, new TreeMap<>());
                }
                modeArenaMap.get(mode).put(arena.getIdentifier(), arena);
            }
        }
    }

    public static int getArenaCount(String mode) {
        return modeArenaMap.get(mode).size();
    }

    public static Arena getArena(String mode, String arenaName) {
        return modeArenaMap.get(mode).get(arenaName);
    }

    public static void refreshArena() {

    }

}
