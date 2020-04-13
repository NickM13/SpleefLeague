/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.Dimension;
import com.spleefleague.core.util.Position;
import com.spleefleague.core.util.database.DBEntity;
import com.spleefleague.core.world.GameWorld;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class Arena extends DBEntity {
    
    private static final Map<ArenaMode, Map<String, Arena>> ARENAS = new HashMap<>();
    
    @DBField
    protected String creator;
    // name is just displayName without spaces
    protected String displayName;
    protected String name;
    @DBField
    protected String description;
    @DBField
    protected Boolean isDefault;
    
    protected ArenaMode mode;
    
    @DBField
    protected Boolean paused;
    
    protected World world;
    
    @DBField
    protected Integer teamCount;
    @DBField
    protected Integer teamSize = 1;
    
    protected List<Location> spawns = new ArrayList<>();
    @DBField
    protected Boolean tpBackSpectators;
    protected Location spectatorSpawn = null;
    
    // Border is used for boundaries of match
    protected List<Dimension> border = new ArrayList<>();
    
    // Fake worlds are for spleef and superjump, real worlds allow for
    // projectiles to interact with blocks
    protected int ongoingMatches = 0;
    protected int ongoingQueues = 0;
    
    @DBLoad(fieldname="border")
    protected void loadBorder(Document doc) {
        Dimension dim = new Dimension();
        dim.load(doc);
        border.add(dim);
    }
    @DBLoad(fieldname="border")
    protected void loadBorders(List<Document> docs) {
        docs.forEach(doc -> {
            Dimension dim = new Dimension();
            dim.load(doc);
            border.add(dim);
        });
    }
    
    public String getCreator() {
        return creator;
    }
    @DBLoad(fieldname="name")
    protected void setName(String displayName) {
        this.displayName = displayName;
        name = displayName.replaceAll("\\s", "");
    }
    public String getName() {
        return name;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getDescription() {
        String desc = description;
        desc += "\n" + "Queued: " + getOngoingQueues();
        desc += "\n" + "Matches: " + getOngoingMatches();
        return desc;
    }
    
    public int getTeamSize() {
        return teamSize;
    }
    
    public List<Dimension> getBorders() {
        return border;
    }
    
    public int getDifficulty() {
        return 0;
    }
    
    @DBLoad(fieldname="world")
    protected void setWorld(String worldName) {
        if (worldName == null || worldName.equals("")) {
            world = Core.DEFAULT_WORLD;
        } else {
            world = Bukkit.getWorld(worldName);
            if (world == null) {
                world = Core.DEFAULT_WORLD;
            }
        }
        for (Location spawn : spawns) {
            spawn.setWorld(world);
        }
        if (spectatorSpawn != null) spectatorSpawn.setWorld(world);
    }
    @DBLoad(fieldname="spectatorSpawn")
    protected void setSpectatorSpawn(Position pos) {
        spectatorSpawn = pos.asLocation(world != null ? world : Core.DEFAULT_WORLD);
    }
    
    public boolean hasTpBackSpectators() {
        return tpBackSpectators;
    }
    public Location getSpectatorSpawn() {
        return spectatorSpawn;
    }
    
    protected void setMode(String mode) {
        this.mode = ArenaMode.getArenaMode(mode);
    }
    public ArenaMode getMode() {
        return mode;
    }
    
    public void setPaused(boolean state) {
        paused = state;
    }
    public boolean isPaused() {
        return paused;
    }
    
    public World getWorld() {
        if (world == null) {
            return spawns.get(0).getWorld();
        } else {
            return world;
        }
    }
    
    public void incrementMatches() {
        ongoingMatches++;
    }
    public void decrementMatches() {
        ongoingMatches--;
    }
    public int getOngoingMatches() {
        return ongoingMatches;
    }
    public boolean isAvailable() {
        return true;
    }
    
    public void incrementQueues() {
        ongoingQueues++;
    }
    public void decrementQueues() {
        ongoingQueues--;
    }
    public int getOngoingQueues() {
        return ongoingQueues;
    }
    
    public GameWorld createGameWorld() {
        return new GameWorld(getWorld());
    }

    public List<Location> getSpawns() {
        return spawns;
    }
    
    @DBLoad(fieldname="spawns")
    protected void loadSpawns(List<List> spawnList) {
        for (List spawn : spawnList) {
            Position pos = new Position();
            pos.load(spawn);
            if (world != null) spawns.add(pos.asLocation(world));
            else spawns.add(pos.asLocation(Core.DEFAULT_WORLD));
        }
    }
    
    public InventoryMenuItem createMenu(Consumer<CorePlayer> queueAction) {
        return InventoryMenuAPI.createItem()
                .setName(getDisplayName())
                .setDescription(cp -> getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(queueAction);
    }
    
    public Location getPostGameWarp() {
        return spectatorSpawn;
    }
    
    public static Map<String, Arena> getUnpausedArenas(ArenaMode mode) {
        if (!ARENAS.containsKey(mode))
            return Collections.EMPTY_MAP;
        Map<String, Arena> arenas = new TreeMap<>();
        
        for (Map.Entry<String, Arena> arena : ARENAS.get(mode).entrySet()) {
            if (!arena.getValue().isPaused()) {
                arenas.put(arena.getKey(), arena.getValue());
            }
        }
        
        return arenas;
    }
    
    public static Map<String, Arena> getArenas(ArenaMode mode) {
        if (!ARENAS.containsKey(mode)) 
            return Collections.EMPTY_MAP;
        return ARENAS.get(mode);
    }
    
    public static Set<String> getArenaNames(ArenaMode mode) {
        if (!ARENAS.containsKey(mode)) {
            return Collections.EMPTY_SET;
        }
        Set<String> arenas = new HashSet<>();
        for (Map.Entry<String, Arena> arena : ARENAS.get(mode).entrySet()) {
            if (!arena.getValue().isPaused()) {
                arenas.add(arena.getKey());
            }
        }
        return arenas;
    }

    public static Arena getRandomArena(ArenaMode mode) {
        Map<String, Arena> arenas = getUnpausedArenas(mode);
        if (arenas.isEmpty()) return null;
        Random generator = new Random();
        Object[] values = arenas.values().toArray();
        return (Arena)(values[generator.nextInt(values.length)]);
    }
    
    public static Arena getByName(String name, ArenaMode mode) {
        if (name == null) return null;
        Map<String, Arena> arenas = ARENAS.get(mode);
        if (arenas != null) {
            if (name.equals("")) {
                return getRandomArena(mode);
            } else {
                return arenas.get(name.toLowerCase());
            }
        }
        return null;
    }
    
    protected static boolean loadArena(Document arenaDoc, ArenaMode mode) {
        if (!ARENAS.containsKey(mode)) ARENAS.put(mode, new TreeMap<>());
        try {
            Arena arena = mode.getArenaClass().newInstance();
            arena.load(arenaDoc);
            ARENAS.get(mode).put(arena.getName().toLowerCase(), arena);
            return true;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    protected static int loadArenas(Iterator<Document> itArena, ArenaMode mode) {
        if (mode == null || mode.getArenaClass() == null) return 0;
        int successCounter = 0;
        if (!ARENAS.containsKey(mode)) ARENAS.put(mode, new TreeMap<>());
        while (itArena.hasNext()) {
            Document adoc = itArena.next();
            try {
                Arena arena = mode.getArenaClass().newInstance();
                arena.load(adoc);
                arena.getMode().addRequiredTeamSize(arena.getTeamSize());
                ARENAS.get(mode).put(arena.getName().toLowerCase(), arena);
                successCounter++;
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return successCounter;
    }
    
    protected static int loadArenas(List<Document> arenaDocs, ArenaMode mode) {
        if (mode == null || mode.getArenaClass() == null) return 0;
        int successCounter = 0;
        if (!ARENAS.containsKey(mode)) ARENAS.put(mode, new TreeMap<>());
        for (Document adoc : arenaDocs) {
            try {
                Arena arena = mode.getArenaClass().newInstance();
                arena.load(adoc);
                arena.getMode().addRequiredTeamSize(arena.getTeamSize());
                ARENAS.get(mode).put(arena.getName().toLowerCase(), arena);
                successCounter++;
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
        return successCounter;
    }
    
}
