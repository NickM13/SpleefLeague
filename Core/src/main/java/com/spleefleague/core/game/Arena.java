/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.core.world.game.GameWorld;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Arena is a set of variables loaded from a specified Database<br>
 * Arena is Used in Battle<br>
 * <br>
 * Arena Document:<br>
 * {<br>
 * identifier:      <i>required</i>, Identifier name for commands<br>
 * name:            <i>required</i>, Display name
 * description:     <i>optional</i>, Description for arena<br>
 * teamCount:       <i>optional</i>, Used for dynamically sized modes, number of teams (if team size is 1 this is number of players)<br>
 * rated:           <i>optional</i>, Default true, Whether arena will apply an elo rating or not afterward<br>
 * queued:          <i>optional</i>, Default true, If false, this arena can only be entered through challenges<br>
 * paused:          <i>optional</i>, Default false, If true, arena cannot be played on<br>
 * borders:         <i>optional</i>, List of dimensions that the arena is contained in<br>
 * goals:           <i>optional</i>, List of dimensions that the arena defines as end points (SuperJump)<br>
 * spectatorSpawn:  <i>optional</i>, Spawn location of spectators<br>
 * modes:           <i>required</i>, List of mode names<br>
 * spawns:          <i>optional</i>, List of spawn positions for battlers<br>
 * checkpoints:     <i>optional</i>, List of checkpoint positions<br>
 * structures:      <i>optional</i>, List of build structure names<br>
 * }
 *
 * @author NickM13
 */
public class Arena extends DBEntity {

    @DBField
    protected String name;
    @DBField
    protected String description = "";

    @DBField
    protected Set<String> modes;

    @DBField
    protected Boolean paused = false;

    protected World world = Core.DEFAULT_WORLD;

    @DBField
    protected Integer teamCount = 1;
    @DBField
    protected Integer teamSize = 1;

    protected List<Position> spawns = new ArrayList<>();
    protected List<Position> checkpoints = new ArrayList<>();
    @DBField
    protected Position spectatorSpawn = null;

    protected List<Position> scoreboards = new ArrayList<>();

    @DBField
    protected Material displayItem = Material.MAP;
    @DBField
    protected Integer displayCmd = 0;

    protected List<Dimension> borders = new ArrayList<>();

    protected List<Dimension> goals = new ArrayList<>();

    @DBField
    protected Set<String> structures;
    @DBField
    protected Position origin = new Position();

    protected int ongoingMatches = 0;
    protected int ongoingQueues = 0;

    public Arena() {

    }

    public Arena(String identifier, String name) {
        this.identifier = identifier;
        setName(name);
        modes = new HashSet<>();
        structures = new HashSet<>();
        paused = false;
    }

    public void cloneFrom(Arena from) {
        this.name = from.getName() + "_copy";
        this.description = from.getDescription();
        this.modes = Sets.newHashSet(from.getModes());
        this.paused = from.isPaused();
        this.teamCount = from.getTeamCount();
        this.teamSize = from.getTeamSize();
        this.spawns = Lists.newArrayList(from.getSpawns());
        this.checkpoints = Lists.newArrayList(from.getCheckpoints());
        this.spectatorSpawn = from.getSpectatorSpawnPosition();
        this.displayItem = from.getDisplayItem();
        this.displayCmd = from.getDisplayCmd();
        this.borders = Lists.newArrayList(from.getBorders());
        this.goals = Lists.newArrayList(from.getGoals());
        this.structures = Sets.newHashSet(from.getStructureNames());
        this.origin = from.getOrigin();
    }

    @DBSave(fieldName = "borders")
    private List<Document> saveBorders() {
        return borders
                .stream()
                .map(Dimension::save)
                .collect(Collectors.toList());
    }

    @DBLoad(fieldName = "borders")
    private void loadBorders(List<Document> docs) {
        docs.forEach(doc -> borders.add(new Dimension(doc)));
    }

    @DBSave(fieldName = "goals")
    private List<Document> saveGoals() {
        return goals
                .stream()
                .map(Dimension::save)
                .collect(Collectors.toList());
    }

    @DBLoad(fieldName = "goals")
    private void loadGoals(List<Document> docs) {
        docs.forEach(doc -> goals.add(new Dimension(doc)));
    }

    @DBLoad(fieldName = "spawns")
    private void loadSpawns(List<List<?>> spawnLists) {
        for (List<?> spawnList : spawnLists) {
            spawns.add(new Position(spawnList));
        }
    }

    @DBSave(fieldName = "spawns")
    private List<List<?>> saveSpawns() {
        List<List<?>> spawnLists = new ArrayList<>();
        for (Position spawn : spawns) {
            spawnLists.add(spawn.save());
        }
        return spawnLists;
    }

    @DBLoad(fieldName = "scoreboards")
    private void loadScoreboards(List<List<?>> scoreboardLists) {
        for (List<?> scoreboardList : scoreboardLists) {
            scoreboards.add(new Position(scoreboardList));
        }
    }

    @DBSave(fieldName = "scoreboards")
    private List<List<?>> saveScoreboards() {
        List<List<?>> scoreboardLists = new ArrayList<>();
        for (Position scoreboard : scoreboards) {
            scoreboardLists.add(scoreboard.save());
        }
        return scoreboardLists;
    }

    @DBLoad(fieldName = "checkpoints")
    private void loadCheckpoints(List<List<?>> checkpointLists) {
        for (List<?> checkpointList : checkpointLists) {
            checkpoints.add(new Position(checkpointList));
        }
    }

    @DBSave(fieldName = "checkpoints")
    private List<List<?>> saveCheckpoints() {
        List<List<?>> checkpointList = new ArrayList<>();
        for (Position checkpoint : checkpoints) {
            checkpointList.add(checkpoint.save());
        }
        return checkpointList;
    }

    /**
     * Set the name and displayName of an arena
     *
     * @param displayName Display name
     */
    public void setName(String displayName) {
        this.name = displayName;
    }

    /**
     * Get the display name of an arena
     *
     * @return Arena Display Name
     */
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the formatted description of an arena, including
     * number of players in queue and number of ongoing matches
     *
     * @return Description
     */
    public String getMenuDescription() {
        String desc = description;
        //desc += ChatColor.GRAY + "" + ChatColor.BOLD + "\n\nIn Queue: " + ChatColor.GOLD + getOngoingQueues();
        //desc += "\n&6Matches: " + getOngoingMatches();
        return desc;
    }

    public void setDescription(String description) {
        this.description = description;
        Arenas.saveArenaDB(this);
    }

    public Material getDisplayItem() {
        return displayItem;
    }

    public int getDisplayCmd() {
        return displayCmd;
    }

    public void setDisplayItem(Material displayItem, int cmd) {
        this.displayItem = displayItem;
        this.displayCmd = cmd;
        Arenas.saveArenaDB(this);
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

    public boolean setTeamSize(int teamSize) {
        if (teamSize > 0 && teamSize <= 8) {
            this.teamSize = teamSize;
            Arenas.saveArenaDB(this);
            return true;
        }
        return false;
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
        Arenas.saveArenaDB(this);
    }

    public void removeBorder(int id) {
        borders.remove(id);
        Arenas.saveArenaDB(this);
    }

    /**
     * Get the borders of this arena
     *
     * @return Dimension List
     */
    public List<Dimension> getGoals() {
        return goals;
    }

    public void addGoal(Dimension goal) {
        goals.add(goal);
        Arenas.saveArenaDB(this);
    }

    public void removeGoal(int id) {
        goals.remove(id);
        Arenas.saveArenaDB(this);
    }

    /**
     * Set the location where Spectators are spawned
     *
     * @param pos Spectator Spawn
     */
    public void setSpectatorSpawn(Position pos) {
        spectatorSpawn = pos;
        Arenas.saveArenaDB(this);
    }

    /**
     * Get the location where Spectators are spawned
     *
     * @return Spectator Spawn
     */
    public Location getSpectatorSpawn() {
        Position pos = spectatorSpawn;
        return pos != null ? pos.toLocation(getWorld()) : null;
    }

    public Position getSpectatorSpawnPosition() {
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
        return world == null ? Core.DEFAULT_WORLD : world;
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
     * @return Battler Spawns List
     */
    public List<Position> getSpawns() {
        return spawns;
    }

    public void removeSpawn(int index) {
        spawns.remove(index);
        Arenas.saveArenaDB(this);
    }

    public void addSpawn(Position spawnPos) {
        spawns.add(spawnPos);
        Arenas.saveArenaDB(this);
    }

    public boolean insertSpawn(Position spawnPos, int index) {
        if (index < 0 || index > spawns.size()) return false;
        spawns.add(index, spawnPos);
        Arenas.saveArenaDB(this);
        return true;
    }

    /**
     * @return Checkpoint Position List
     */
    public List<Position> getCheckpoints() {
        return checkpoints;
    }

    public void removeCheckpoint(int index) {
        checkpoints.remove(index);
        Arenas.saveArenaDB(this);
    }

    public void addCheckpoint(Position spawnPos) {
        checkpoints.add(spawnPos);
        Arenas.saveArenaDB(this);
    }

    public boolean insertCheckpoint(Position spawnPos, int index) {
        if (index < 0 || index > checkpoints.size()) return false;
        checkpoints.add(index, spawnPos);
        Arenas.saveArenaDB(this);
        return true;
    }

    /**
     * Create a menu item for this arena
     *
     * @param queueAction Click Action
     * @return Inventory Menu Item
     */
    public InventoryMenuItem createMenu(Consumer<CorePlayer> queueAction) {
        return InventoryMenuAPI.createItemDynamic()
                .setName("&a&l" + getName())
                .setDescription(cp -> getMenuDescription())
                .setDisplayItem(displayItem, displayCmd)
                .setAction(queueAction);
    }

    /**
     * Get the location for players after a game ends
     * used if they have Arena PostWarp enabled
     * <p>
     * TODO: Right now this is just spectatorSpawn, should it be changed?
     *
     * @return Post Game Location
     */
    public Location getPostGameWarp() {
        if (spectatorSpawn == null) return null;
        return spectatorSpawn.toLocation(world);
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

    public BuildStructure getRandomStructure(String startStr) {
        List<String> structures = new ArrayList<>();
        for (String structure : this.structures) {
            if (structure.startsWith(startStr)) {
                structures.add(structure);
            }
        }
        return structures.isEmpty() ? null : BuildStructures.get(structures.get((new Random()).nextInt(structures.size())));
    }

    public void setOrigin(Position position) {
        origin = position;
        Arenas.saveArenaDB(this);
    }

    public void addScoreboard(Position position) {
        scoreboards.add(position);
        Arenas.saveArenaDB(this);
    }

    public void clearScoreboards() {
        scoreboards.clear();
        Arenas.saveArenaDB(this);
    }

    public List<Position> getScoreboards() {
        return scoreboards;
    }

    public Position getOrigin() {
        return origin;
    }

}
