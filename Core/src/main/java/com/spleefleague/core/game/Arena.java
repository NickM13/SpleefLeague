/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.database.variable.DBEntity;
import com.spleefleague.core.world.game.GameWorld;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
 * Arena is a set of variables loaded from a specified Database
 * Arena is Used in Battle
 *
 * @author NickM13
 */
public class Arena extends DBEntity {

    /**
     * ARENAS contains every loaded arena
     */
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

    @DBLoad(fieldName ="border")
    private void loadBorder(Document doc) {
        Dimension dim = new Dimension();
        dim.load(doc);
        border.add(dim);
    }
    @DBLoad(fieldName ="border")
    private void loadBorders(List<Document> docs) {
        docs.forEach(doc -> {
            Dimension dim = new Dimension();
            dim.load(doc);
            border.add(dim);
        });
    }

    /**
     * Get the name of the creator of this arena
     *
     * @return Arena Creator
     */
    public String getCreator() {
        return creator;
    }
    @DBLoad(fieldName ="name")
    private void setName(String displayName) {
        this.displayName = displayName;
        name = displayName.replaceAll("\\s", "");
    }

    /**
     * Get the name of an arena used for indexing (no spaces)
     *
     * @return Arena Name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the displayName of an arena (includes spaces)
     *
     * @return Arena Display Name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the formatted descriptoin of an arena, including
     * number of players in queue and number of ongoing matches
     */
    public String getDescription() {
        String desc = description;
        desc += "\n" + "Queued: " + getOngoingQueues();
        desc += "\n" + "Matches: " + getOngoingMatches();
        return desc;
    }

    /**
     * Get the required number of players per team
     *
     * @return Team Size
     */
    public int getTeamSize() {
        return teamSize;
    }

    /**
     * Get the borders of this arena
     *
     * @return Dimension List
     */
    public List<Dimension> getBorders() {
        return border;
    }

    @DBLoad(fieldName ="world")
    private void setWorld(String worldName) {
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

    @DBLoad(fieldName ="spectatorSpawn")
    private void setSpectatorSpawn(Position pos) {
        spectatorSpawn = pos.asLocation(world != null ? world : Core.DEFAULT_WORLD);
    }

    /**
     * Whether spectators should be teleported if they
     * leave the spectator boundaries
     *
     * @return Tp Back Spectators?
     */
    public boolean hasTpBackSpectators() {
        return tpBackSpectators;
    }

    /**
     * Get the location Spectators are teleported to
     *
     * @return Spectator Spawn
     */
    public Location getSpectatorSpawn() {
        return spectatorSpawn;
    }

    /**
     * Set the mode of an arena
     *
     * @param mode Mode Name
     */
    protected void setMode(String mode) {
        this.mode = ArenaMode.getArenaMode(mode);
    }

    /**
     * Get the mode of an arena
     *
     * @return Arena Mode
     */
    public ArenaMode getMode() {
        return mode;
    }

    /**
     * Set arena's pause state
     *
     * @param state Paused
     */
    public void setPaused(boolean state) {
        paused = state;
    }

    /**
     * Whether arena's queues are paused or not
     *
     * @return Paused
     */
    private boolean isPaused() {
        return paused;
    }

    /**
     * Get the world that this arena is played in
     *
     * @return World
     */
    public World getWorld() {
        if (world == null) {
            return spawns.get(0).getWorld();
        } else {
            return world;
        }
    }

    /**
     * Add ongoing match
     */
    public void incrementMatches() {
        ongoingMatches++;
    }

    /**
     * Subtract ongoing match
     */
    public void decrementMatches() {
        ongoingMatches--;
    }

    /**
     * @return Ongoing Matches
     */
    public int getOngoingMatches() {
        return ongoingMatches;
    }

    /**
     * Returns whether arena can be used, for disabling arenas
     * during maintenance times
     *
     * @return Arena Availability
     */
    public boolean isAvailable() {
        return !isPaused();
    }

    /**
     * Add queued player
     */
    public void incrementQueues() {
        ongoingQueues++;
    }

    /**
     * Subtract queued player
     */
    public void decrementQueues() {
        ongoingQueues--;
    }

    /**
     * Get total number of queued players for this arena
     *
     * @return Queued Players
     */
    public int getOngoingQueues() {
        return ongoingQueues;
    }

    /**
     * Create a GameWorld for this arena to be build in
     *
     * @return New GameWorld
     */
    public GameWorld createGameWorld() {
        return new GameWorld(getWorld());
    }

    /**
     * Possible spawn locations for battlers
     *
     * @return Spawn List
     */
    public List<Location> getSpawns() {
        return spawns;
    }

    @DBLoad(fieldName ="spawns")
    private void loadSpawns(List<List<?>> spawnList) {
        for (List<?> spawn : spawnList) {
            Position pos = new Position();
            pos.load(spawn);
            if (world != null) spawns.add(pos.asLocation(world));
            else spawns.add(pos.asLocation(Core.DEFAULT_WORLD));
        }
    }

    /**
     * Create a menu item for this arena
     *
     * @param queueAction Click Action
     * @return Inventory Menu Item
     */
    public InventoryMenuItem createMenu(Consumer<CorePlayer> queueAction) {
        return InventoryMenuAPI.createItem()
                .setName(getDisplayName())
                .setDescription(cp -> getDescription())
                .setDisplayItem(cp -> { return new ItemStack(Material.FILLED_MAP); })
                .setAction(queueAction);
    }

    /**
     * Get the location for players after a game ends
     * used if they have Arena PostWarp enabled
     *
     * TODO: Right now this is just spectatorSpawn, should it be changed?
     *
     * @return Post Game Location
     */
    public Location getPostGameWarp() {
        return spectatorSpawn;
    }

    /**
     * Get all arenas for a specific mode that are available
     *
     * @param mode Arena Mode
     * @return Available Arenas
     */
    public static Map<String, Arena> getUnpausedArenas(ArenaMode mode) {
        if (!ARENAS.containsKey(mode))
            return new HashMap<>();
        Map<String, Arena> arenas = new TreeMap<>();
        
        for (Map.Entry<String, Arena> arena : ARENAS.get(mode).entrySet()) {
            if (arena.getValue().isAvailable()) {
                arenas.put(arena.getKey(), arena.getValue());
            }
        }
        
        return arenas;
    }

    /**
     * Get all arenas for a specific mode
     *
     * @param mode Arena Mode
     * @return Arenas
     */
    public static Map<String, Arena> getArenas(ArenaMode mode) {
        if (!ARENAS.containsKey(mode)) 
            return new HashMap<>();
        return ARENAS.get(mode);
    }

    /**
     * Get a set of names of all available arenas for a mode
     *
     * @param mode Arena Mode
     * @return String Set
     */
    public static Set<String> getArenaNames(ArenaMode mode) {
        if (!ARENAS.containsKey(mode)) {
            return new HashSet<>();
        }
        Set<String> arenas = new HashSet<>();
        for (Map.Entry<String, Arena> arena : ARENAS.get(mode).entrySet()) {
            if (arena.getValue().isAvailable()) {
                arenas.add(arena.getKey());
            }
        }
        return arenas;
    }

    /**
     * Returns a random arena of a mode
     *
     * @param mode Arena Mode
     * @return Arena
     */
    public static Arena getRandomArena(ArenaMode mode) {
        Map<String, Arena> arenas = getUnpausedArenas(mode);
        if (arenas.isEmpty()) return null;
        Random generator = new Random();
        Object[] values = arenas.values().toArray();
        return (Arena)(values[generator.nextInt(values.length)]);
    }

    /**
     * Returns an arena by name and mode
     *
     * @param name Arena Name
     * @param mode Arena Mode
     * @return Arena
     */
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

    /**
     * Load arena into ARENAS list from document
     *
     * @param arenaDoc Document
     * @param mode Arena Mode
     * @return Success
     */
    protected static boolean loadArena(Document arenaDoc, ArenaMode mode) {
        if (!ARENAS.containsKey(mode)) ARENAS.put(mode, new TreeMap<>());
        try {
            Arena arena = mode.getArenaClass().getDeclaredConstructor().newInstance();
            arena.load(arenaDoc);
            ARENAS.get(mode).put(arena.getName().toLowerCase(), arena);
            return true;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Load arenas into ARENAS list from an iterator of docs
     *
     * @param itArena Document Iterator
     * @param mode Arena Mode
     * @return Num of successes
     */
    protected static int loadArenas(Iterator<Document> itArena, ArenaMode mode) {
        if (mode == null || mode.getArenaClass() == null) return 0;
        int successCounter = 0;
        if (!ARENAS.containsKey(mode)) ARENAS.put(mode, new TreeMap<>());
        while (itArena.hasNext()) {
            Document adoc = itArena.next();
            try {
                Arena arena = mode.getArenaClass().getDeclaredConstructor().newInstance();
                arena.load(adoc);
                arena.getMode().addRequiredTeamSize(arena.getTeamSize());
                ARENAS.get(mode).put(arena.getName().toLowerCase(), arena);
                successCounter++;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return successCounter;
    }

    /**
     * Load arenas into ARENAS list from a list of docs
     *
     * @param arenaDocs List of Documents
     * @param mode Arena Mode
     * @return Num of successes
     */
    protected static int loadArenas(List<Document> arenaDocs, ArenaMode mode) {
        if (mode == null || mode.getArenaClass() == null) return 0;
        int successCounter = 0;
        if (!ARENAS.containsKey(mode)) ARENAS.put(mode, new TreeMap<>());
        for (Document adoc : arenaDocs) {
            try {
                Arena arena = mode.getArenaClass().getDeclaredConstructor().newInstance();
                arena.load(adoc);
                arena.getMode().addRequiredTeamSize(arena.getTeamSize());
                ARENAS.get(mode).put(arena.getName().toLowerCase(), arena);
                successCounter++;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
        return successCounter;
    }
    
}
