/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.annotation.DBSave;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.database.variable.DBEntity;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
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
import java.util.stream.Collectors;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * Arena is a set of variables loaded from a specified Database<br>
 * Arena is Used in Battle<br>
 * <br>
 * Arena Document:<br>
 * {<br>
 *     name:            <i>required</i>, Identifier and display name for arena<br>
 *     description:     <i>optional</i>, Description for arena<br>
 *     teamCount:       <i>optional</i>, Used for dynamically sized modes, number of teams (if team size is 1 this is number of players)<br>
 *     rated:           <i>optional</i>, Default true, Whether arena will apply an elo rating or not afterward<br>
 *     queued:          <i>optional</i>, Default true, If false, this arena can only be entered through challenges<br>
 *     paused:          <i>optional</i>, Default false, If true, arena cannot be played on<br>
 *     border:          <i>optional</i>, List of dimensions that the arena is contained in<br>
 *     spectatorSpawn:  <i>optional</i>, Spawn location of spectators<br>
 *     modes:           <i>required</i>, List of mode names<br>
 *     spawns:          <i>optional</i>, List of spawn locations for battlers<br>
 *     structures:      <i>optional</i>, List of build structure names<br>
 * }
 *
 * @author NickM13
 */
public class Arena extends DBEntity {
    
    protected String displayName;
    protected String name; // name is just displayName without spaces for indexing with commands
    @DBField protected String description = "";
    
    @DBField protected Set<String> modes;
    
    @DBField protected Boolean paused = false;
    
    protected World world = Core.DEFAULT_WORLD;
    
    @DBField protected Integer teamCount = 1;
    @DBField protected Integer teamSize = 1;
    
    protected List<Location> spawns = new ArrayList<>();
    protected Location spectatorSpawn = null;
    
    protected List<Dimension> borders = new ArrayList<>();
    private static final int SPECTATOR_EXPAND = 20;
    protected List<Dimension> spectatorBorders = new ArrayList<>();
    private static final int GLOBAL_SPECTATOR_EXPAND = 40;
    protected List<Dimension> globalSpectatorBorders = new ArrayList<>();
    
    @DBField protected Set<String> structures;
    
    protected int ongoingMatches = 0;
    protected int ongoingQueues = 0;
    
    public Arena() {
    
    }
    
    public Arena(String name) {
        setName(name);
        modes = new HashSet<>();
        structures = new HashSet<>();
        paused = false;
    }

    @DBLoad(fieldName ="borders")
    private void loadBorders(List<Document> docs) {
        docs.forEach(doc -> {
            Dimension dim = new Dimension();
            dim.load(doc);
            borders.add(dim);
        });
        refreshSpectatorBorders();
    }
    
    @DBSave(fieldName="borders")
    private List<Document> saveBorders() {
        return borders
                .stream()
                .map(Dimension::save)
                .collect(Collectors.toList());
    }
    
    private void refreshSpectatorBorders() {
        spectatorBorders.clear();
        globalSpectatorBorders.clear();
        for (Dimension dim : borders) {
            spectatorBorders.add(dim.expand(SPECTATOR_EXPAND));
            globalSpectatorBorders.add(dim.expand(GLOBAL_SPECTATOR_EXPAND));
        }
    }
    
    /**
     * Set the name and displayName of an arena
     *
     * @param displayName Display name
     */
    @DBLoad(fieldName ="name")
    public void setName(String displayName) {
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
    @DBSave(fieldName = "name")
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the formatted descriptoin of an arena, including
     * number of players in queue and number of ongoing matches
     *
     * @return Description
     */
    public String getDescription() {
        String desc = description;
        desc += "\n" + "Queued: " + getOngoingQueues();
        desc += "\n" + "Matches: " + getOngoingMatches();
        return desc;
    }
    
    /**
     * Return the modes this arena is used in
     *
     * @return Set of Modes
     */
    public Set<String> getModes() {
        return modes;
    }
    
    public boolean addMode(String mode) {
        return modes.add(mode);
    }
    
    public boolean removeMode(String mode) {
        return modes.remove(mode);
    }

    /**
     * Get the required number of players per team
     *
     * @return Team Size
     */
    public int getTeamSize() {
        return teamSize;
    }
    
    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
        Arenas.saveArenaDB(this);
    }
    
    /**
     * Get the required number of teams (or players if teamSize = 1)
     *
     * @return Team Size
     */
    public int getTeamCount() {
        return teamCount;
    }
    
    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
        Arenas.saveArenaDB(this);
    }
    
    /**
     * Get the borders of this arena
     *
     * @return Dimension List
     */
    public List<Dimension> getBorders() {
        return borders;
    }
    
    public void addBorder(Dimension border) {
        borders.add(border);
        refreshSpectatorBorders();
        Arenas.saveArenaDB(this);
    }
    
    public void removeBorder(int id) {
        borders.remove(id);
        Arenas.saveArenaDB(this);
    }
    
    /**
     * Get the borders for spectators of this arena
     *
     * @return Dimension List
     */
    public List<Dimension> getSpectatorBorders() {
        return spectatorBorders;
    }
    
    /**
     * Get the area that players will begin auto spectating nearby
     *
     * @return Dimension List
     */
    public List<Dimension> getGlobalSpectatorBorders() {
        return globalSpectatorBorders;
    }

    @DBLoad(fieldName="spectatorSpawn")
    private void loadSpectatorSpawn(Position pos) {
        if (pos == null) {
            spectatorSpawn = null;
        } else {
            spectatorSpawn = pos.asLocation(world != null ? world : Core.DEFAULT_WORLD);
        }
    }
    
    @DBSave(fieldName="spectatorSpawn")
    private Position saveSpectatorSpawn() {
        if (spectatorSpawn == null) return null;
        return new Position(spectatorSpawn);
    }
    
    public void setSpectatorSpawn(Location loc) {
        spectatorSpawn = loc;
        Arenas.saveArenaDB(this);
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
     * For determining whether players should be in GM3 on spectating
     *
     * @return Whether spectatorSpawn exists
     */
    public boolean hasSpectatorSpawn() {
        return spectatorSpawn != null;
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
    
    public void removeSpawn(int index) {
        spawns.remove(index);
        Arenas.saveArenaDB(this);
    }
    
    public void addSpawn(Location spawnLoc) {
        spawns.add(spawnLoc);
        Arenas.saveArenaDB(this);
    }
    
    public boolean insertSpawn(Location spawnLoc, int index) {
        if (index < 0 || index > spawns.size()) return false;
        spawns.add(index, spawnLoc);
        Arenas.saveArenaDB(this);
        return true;
    }
    
    @DBLoad(fieldName ="spawns")
    private void loadSpawns(List<List<?>> spawnLists) {
        for (List<?> spawnList : spawnLists) {
            Position pos = new Position();
            pos.load(spawnList);
            if (world != null) spawns.add(pos.asLocation(world));
            else spawns.add(pos.asLocation(Core.DEFAULT_WORLD));
        }
    }
    
    @DBSave(fieldName ="spawns")
    private List<List<?>> saveSpawns() {
        List<List<?>> spawnLists = new ArrayList<>();
        for (Location spawn : spawns) {
            Position pos = new Position(spawn);
            spawnLists.add(pos.save());
        }
        return spawnLists;
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
    
    public boolean addStructure(String structureName) {
        if (structures.add(structureName)) {
            Arenas.saveArenaDB(this);
            return true;
        }
        return false;
    }
    
    public boolean removeStructure(String structureName) {
        if (structures.remove(structureName)) {
            Arenas.saveArenaDB(this);
            return true;
        }
        return false;
    }
    
    public Set<String> getStructureNames() {
        return structures;
    }
    
    /**
     * Get the Build Structures for this arena
     *
     * @return List of Build Structures
     */
    public List<BuildStructure> getStructures() {
        List<BuildStructure> buildStructures = new ArrayList<BuildStructure>();
        for (String structure : structures) {
            buildStructures.add(BuildStructures.get(structure));
        }
        return buildStructures;
    }
    
}
