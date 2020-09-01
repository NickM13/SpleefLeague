/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game.battle;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatGroup;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.request.BattleRequest;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.game.GameWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import com.spleefleague.coreapi.database.variable.DBPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nullable;

/**
 * A Battle is an object that contains an arena that the game
 * is played in and a set of players (battlers, spectators)
 *
 * @author NickM13
 */
public abstract class Battle<BP extends BattlePlayer> {

    protected CorePlugin<?> plugin;
    
    protected org.bukkit.GameMode gameMode = org.bukkit.GameMode.ADVENTURE;
    protected GameWorld gameWorld;
    protected BattleMode battleMode;
    
    protected ChatGroup chatGroup;
    
    // Arena to play battle on
    protected final Arena arena;

    private final Class<BP> battlePlayerClass;
    
    // Some values of Arena that can be modified without changing the arena
    protected List<Dimension> goals = new ArrayList<>();
    protected List<Dimension> borders = new ArrayList<>();
    private static final int SPECTATOR_EXPAND = 20;
    private static final int GLOBAL_SPECTATOR_EXPAND = 40;
    protected List<Dimension> spectatorBorders = new ArrayList<>();
    protected List<Dimension> globalSpectatorBorders = new ArrayList<>();
    
    protected List<Position> checkpoints = new ArrayList<>();
    protected List<Dimension> checkpointAreas = new ArrayList<>();
    protected List<Position> spawns = new ArrayList<>();

    // Collections for players
    protected final List<UUID> waitingPlayers = new ArrayList<>();
    protected final Set<CorePlayer> players = new HashSet<>();
    protected final Set<CorePlayer> spectators = new HashSet<>();
    protected final Map<CorePlayer, BP> battlers = new HashMap<>();
    protected final List<BP> sortedBattlers = new ArrayList<>();
    protected final Set<BP> remainingPlayers = new HashSet<>();
    
    private final Map<String, BattleRequest> battleRequests = new HashMap<>();
    
    // Ongoing battle stats
    protected boolean ongoing;
    protected boolean finished = false;
    protected boolean waiting = true;
    protected long startedTime;
    protected long roundStartTime = 0;
    protected boolean frozen = false;
    
    // Round countdown
    protected static final int COUNTDOWN = 3;
    protected int countdown = 0;

    public Battle(CorePlugin<?> plugin, List<UUID> players, Arena arena, Class<BP> battlePlayerClass, BattleMode battleMode) {
        this.plugin = plugin;
        this.arena = arena;
        this.battlePlayerClass = battlePlayerClass;
        this.battleMode = battleMode;
        this.setGoals(arena.getGoals());
        this.setBorders(arena.getBorders());
        this.setCheckpoints(arena.getCheckpoints());
        this.spawns.addAll(arena.getSpawns());
        this.gameWorld = arena.createGameWorld();
        this.chatGroup = new ChatGroup(plugin.getChatPrefix());
        this.waitingPlayers.addAll(players);
    }

    /**
     * Start a battle
     */
    public final void startBattle() {
        if (!waiting) {
            arena.incrementMatches();
            battleMode.addBattle(this);
            startedTime = System.currentTimeMillis();
            battlers.keySet().forEach(cp -> addPlayer(cp, BattleState.BATTLER));
            setupBattleRequests();
            setupBaseSettings();
            setupBattlers();
            setupScoreboard();
            sendStartMessage();
            ongoing = true;
            startRound();
        }
    }

    public final void waitForPlayers() {
        waiting = true;
    }
    
    /**
     * Called in startBattle()<br>
     * Initializes all valid battle requests for a battle
     */
    protected abstract void setupBattleRequests();

    /**
     * Called in startBattle()<br>
     * Initialize base battle settings such as GameWorld tools
     */
    protected abstract void setupBaseSettings();
    
    /**
     * Called in startBattle()<br>
     * Initialize scoreboard
     */
    protected abstract void setupScoreboard();

    /**
     * Called in startBattle()<br>
     * Initialize battler attributes such as spawn location
     */
    protected abstract void setupBattlers();

    /**
     * Called in startBattle<br>
     * Send a message on the start of a battle
     */
    protected abstract void sendStartMessage();

    /**
     * Start a round<br>
     * Resets the field and its players, also used in Reset Request
     */
    public void startRound() {
        if (!ongoing) return;
        gameWorld.clearProjectiles();
        fillField();
        startCountdown();
        resetBattlers();
        updateScoreboard();
        resetRequests();
    }
    
    /**
     * Set the GameMode of a battle (TODO: Is this always Adventure?)
     * @param gameMode GameMode
     */
    public final void setGameMode(org.bukkit.GameMode gameMode) {
        this.gameMode = gameMode;
    }

    protected abstract void fillField();

    /**
     * Called when a battler is spawned into a battle or when
     * they should be respawned
     *
     * @param bp BattlePlayer
     */
    protected final void spawnBattler(BP bp) {
        bp.getCorePlayer().refreshHotbar();
        bp.getCorePlayer().setGameMode(gameMode);
        bp.getPlayer().setWalkSpeed(0.2f);
        bp.respawn();
    }

    /**
     * Called when a battler joins mid-game (if available)
     *
     * @param cp Core Player
     */
    protected abstract void joinBattler(CorePlayer cp);

    /**
     * Adds a player to the battle, creates a Battle Player
     * and sets player's battle state to BATTLER
     *
     * If a player is a party leader and the mode is team based
     * then add all party members to the battle list
     *
     * @param cp Core Player
     */
    public final void addBattler(CorePlayer cp) {
        try {
            if (cp.isInBattle()) {
                cp.getBattle().leavePlayer(cp);
            }
            List<CorePlayer> toBattlefy = new ArrayList<>();
            if (battleMode.getTeamStyle().equals(BattleMode.TeamStyle.TEAM)) {
                toBattlefy.addAll(cp.getParty().getPlayers());
            } else {
                toBattlefy.add(cp);
            }
            for (CorePlayer cp2 : toBattlefy) {
                Constructor<BP> c = battlePlayerClass.getDeclaredConstructor(CorePlayer.class, Battle.class);
                c.setAccessible(true);
                BP bp = c.newInstance(cp2, this);
                bp.setSpawn(getSpawn(battlers.size()).toLocation(arena.getWorld()));
                battlers.put(cp2, bp);
                sortedBattlers.add(bp);
                //spawnBattler(bp);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            CoreLogger.logError(exception);
            //Core.getInstance().getLogger().log(Level.SEVERE, "Battle.java: Failed to create new instance of a battle player " + battlePlayerClass);
        }
    }

    /**
     * Called when a player that is already in this battle player list wants it's battle state changed
     *
     * @param cp Core Player
     * @param toState To Battle State
     */
    public void convertIngamePlayer(CorePlayer cp, BattleState toState) {
        //cp.
    }

    /**
     * Save the battlers stats
     * Called when a battler is removed from the battle
     *
     * @param bp Battle Player
     */
    protected abstract void saveBattlerStats(BP bp);

    /**
     * Removes a battler from the battle without any further checking
     *
     * @param cp Battler Core Player
     */
    public final void removeBattler(CorePlayer cp) {
        if (!battlers.containsKey(cp)) return;
        saveBattlerStats(battlers.get(cp));
        battlers.remove(cp);
        removePlayer(cp);
    }

    /**
     * @return Ongoing Battle State
     */
    public boolean isOngoing() {
        return ongoing;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWaiting() {
        return waiting;
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }
    
    public List<Dimension> getGoals() {
        return goals;
    }
    
    /**
     * @param cp Core Player
     * @return In Goal
     */
    private boolean isInGoal(CorePlayer cp) {
        if (!goals.isEmpty()) {
            Point point = new Point(cp.getPlayer().getLocation());
            for (Dimension goal : goals) {
                if (goal.isContained(point)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Set goal dimensions
     *
     * @param goals List of Dimensions
     */
    public void setGoals(List<Dimension> goals) {
        this.goals = goals;
    }
    
    /**
     * Check if a player is within the bounding boxes of the arena
     *
     * @param cp Core Player
     * @return In Battler Bounds
     */
    private boolean isInBorder(CorePlayer cp) {
        if (borders.isEmpty()) {
            return true;
        }
        Point point = new Point(cp.getPlayer().getLocation());
        for (Dimension border : borders) {
            if (border.isContained(point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a player is within the spectators bounding boxes of the arena
     *
     * @param cp Core Player
     * @return In Spectator Border
     */
    private boolean isInSpectatorBorder(CorePlayer cp) {
        for (Dimension border : spectatorBorders) {
            if (border.isContained(new Point(cp.getPlayer().getLocation()))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if a player is within the global spectator bounding boxes,
     * used to see whether a player should be removed from a battle or
     * for public use added to one
     *
     * @param cp Core Player
     * @return In Border
     */
    public boolean isInGlobalSpectatorBorder(CorePlayer cp) {
        for (Dimension border : globalSpectatorBorders) {
            if (border.isContained(new Point(cp.getPlayer().getLocation()))) {
                return true;
            }
        }
        return false;
    }
    
    public void setBorders(List<Dimension> borders) {
        this.borders = borders;
        this.spectatorBorders.clear();
        this.globalSpectatorBorders.clear();
        for (Dimension dim : borders) {
            this.spectatorBorders.add(dim.expand(SPECTATOR_EXPAND));
            this.globalSpectatorBorders.add(dim.expand(GLOBAL_SPECTATOR_EXPAND));
        }
    }

    /**
     * Returns the closest battler (non-fallen) to the player
     * If no players are alive, returns null
     *
     * @param cp Core Player
     * @return Closest Alive Player (null if none)
     */
    protected final BP getClosestBattler(CorePlayer cp) {
        BP closest = null;
        double closeDist = 0, dist;
        for (BP bp2 : battlers.values()) {
            if (!cp.equals(bp2.getCorePlayer()) && !bp2.isFallen()) {
                dist = cp.getPlayer().getLocation().distance(bp2.getPlayer().getLocation());
                if (closest == null || dist < closeDist) {
                    closest = bp2;
                    closeDist = dist;
                }
            }
        }
        return closest;
    }

    protected void onSpectatorEnter(CorePlayer cp) {
        cp.getPlayer().teleport(arena.getSpectatorSpawn());
    }

    protected void onGlobalSpectatorEnter(CorePlayer cp) {
        cp.getPlayer().teleport(arena.getSpectatorSpawn());
    }

    /**
     * Check the movement of a player based on their battle state
     *
     * Prevents spectators with free roam from leaving the spectator area by
     * teleporting them to the nearest battling player
     *
     * @param cp CorePlayer
     * @param e Player Move Event
     */
    public final void onMove(CorePlayer cp, PlayerMoveEvent e) {
        if (ongoing) {
            if (cp.getBattleState() == BattleState.BATTLER) {
                BP bp = battlers.get(cp);
                if (!bp.isFallen()) {
                    if (!isRoundStarted()) {
                        if (frozen || true) {
                            e.getPlayer().teleport(new Location(e.getFrom().getWorld(),
                                    e.getFrom().getX(),
                                    e.getFrom().getY(),
                                    e.getFrom().getZ(),
                                    e.getTo().getYaw(),
                                    e.getTo().getPitch()));
                        } else if (e.getTo() != null && e.getFrom().getY() != e.getTo().getY()) {
                            e.getPlayer().teleport(new Location(e.getFrom().getWorld(),
                                    e.getTo().getX(),
                                    e.getFrom().getY(),
                                    e.getTo().getZ(),
                                    e.getTo().getYaw(),
                                    e.getTo().getPitch()));
                        }
                    } else if (e.getPlayer().getLocation().getBlock().isLiquid() || !isInBorder(cp)) {
                        failBattler(cp);
                    } else if (isInGoal(cp)) {
                        winBattler(cp);
                    } else {
                        checkCheckpoints(cp);
                    }
                } else {
                    if (!isInSpectatorBorder(cp)) {
                        e.setTo(getClosestBattler(cp).getPlayer().getLocation());
                    }
                }
            } else if (cp.getBattleState() == BattleState.SPECTATOR) {
                if (!cp.getPlayer().getGameMode().equals(org.bukkit.GameMode.CREATIVE) &&
                        !cp.getPlayer().getGameMode().equals(org.bukkit.GameMode.SPECTATOR) &&
                        arena.hasSpectatorSpawn() && (isInBorder(cp) || !isInSpectatorBorder(cp))) {
                    onSpectatorEnter(cp);
                }
            } else if (cp.getBattleState() == BattleState.SPECTATOR_GLOBAL) {
                if (!cp.getPlayer().getGameMode().equals(org.bukkit.GameMode.CREATIVE) && arena.hasSpectatorSpawn() && (isInBorder(cp))) {
                    onGlobalSpectatorEnter(cp);
                }
                if (!isInGlobalSpectatorBorder(cp)) {
                    removeSpectator(cp);
                }
            }
        }
    }

    /**
     * Called when a player right clicks.
     *
     * @param cp CorePlayer
     */
    public void onRightClick(CorePlayer cp) {
        battlers.get(cp).onRightClick();
    }

    /**
     * Called when a battler punches another battler
     *
     * @param cp Core Player
     * @param target Core Player
     */
    public void onPlayerPunch(CorePlayer cp, CorePlayer target) {
        battlers.get(cp).onPlayerPunch(battlers.get(target));
    }

    public void onPlayerHit(CorePlayer cp, CorePlayer target) {
        battlers.get(target).onPlayerHit(battlers.get(cp));
    }

    /**
     * Called when a player breaks a block.
     *
     * @param cp Core Player
     */
    public void onBlockBreak(CorePlayer cp) {
        battlers.get(cp).onBlockBreak();
    }

    public void onSlotChange(CorePlayer cp, int newSlot) {
        battlers.get(cp).onSlotChange(newSlot);
    }

    public void onDropItem(CorePlayer cp) {
        battlers.get(cp).onDropItem();
    }

    public void onSwapItem(CorePlayer cp) {
        battlers.get(cp).onSwapItem();
    }

    public void onStartSneak(CorePlayer cp) {
        battlers.get(cp).onStartSneak();
    }

    public void onStopSneak(CorePlayer cp) {
        battlers.get(cp).onStopSneak();
    }

    /**
     * @param id Spawn Index
     * @return Spawn Location
     */
    protected Position getSpawn(int id) {
        if (id < spawns.size())
            return spawns.get(id);
        return spawns.get(0);
    }
    
    public void setCheckpoints(List<Position> checkpoints) {
        this.checkpoints = checkpoints;
        this.checkpointAreas.clear();
        this.checkpointAreas.addAll(arena.getCheckpoints()
                .stream()
                .map(checkpoint -> new Dimension(checkpoint.add(-0.5, 0, -0.5), checkpoint.add(0.5, 1, 0.5)))
                .collect(Collectors.toList()));
    }
    
    /**
     *
     * @param cp Core Player
     * @return In Checkpoint
     */
    private void checkCheckpoints(CorePlayer cp) {
        if (checkpointAreas.isEmpty()) return;
        BattlePlayer bp = battlers.get(cp);
        Point point = new Point(cp.getLocation());
        for (int i = bp.getCheckpoint() + 1; i < checkpointAreas.size(); i++) {
            if (checkpointAreas.get(i).isContained(point)) {
                bp.setCheckpoint(i);
                return;
            }
        }
    }
    
    public Location getCheckpoint(int id) {
        return checkpoints.get(id).toLocation(arena.getWorld());
    }

    /**
     * Moderator cancel for battles
     */
    public void cancel() {
        chatGroup.sendMessage("Your match was cancelled by a moderator");
        destroy();
    }
    
    /**
     * Returns the chat group of this battle
     *
     * @return Chat Group
     */
    public ChatGroup getChatGroup() {
        return chatGroup;
    }

    /**
     * Adds a player to the battle
     *
     * @param cp CorePlayer
     * @param battleState Battle State
     * @return Success
     */
    private boolean addPlayer(CorePlayer cp, BattleState battleState) {
        if (!players.contains(cp)) {
            if (cp.isInBattle()) {
                cp.getBattle().leavePlayer(cp);
            }
            players.add(cp);
            gameWorld.addPlayer(cp);
            chatGroup.addPlayer(cp);
            cp.joinBattle(this, battleState);
            cp.getPlayer().getInventory().setHeldItemSlot(0);
            cp.getPlayer().getInventory().clear();
            cp.refreshHotbar();
            return true;
        }
        return false;
    }

    /**
     * Removes a player from the battle
     *
     * @param cp Core Player
     * @return Success
     */
    protected final boolean removePlayer(CorePlayer cp) {
        if (players.contains(cp)) {
            chatGroup.removePlayer(cp);
            gameWorld.removePlayer(cp);
            players.remove(cp);
            cp.leaveBattle(arena.getPostGameWarp());
            //Core.getInstance().returnToHub(cp);
            return true;
        }
        return false;
    }
    
    /**
     * End a round with a determined winner
     *
     * @param winner Winner
     */
    protected abstract void endRound(BP winner);
    
    /**
     * End a battle with a determined winner
     *
     * @param winner Winner
     */
    public abstract void endBattle(BP winner);

    protected enum OreType {
        NONE, COMMON, RARE, EPIC, LEGENDARY;
    }

    /**
     * Get a random ore based on percentage weights (should add up to less than 1)
     *
     * @param common common ore
     * @param rare rare ore
     * @param epic epic ore
     * @param legendary legendary ore
     * @return Ore Type
     */
    protected static OreType getRandomOre(double common, double rare, double epic, double legendary) {
        double r = Math.random();
        if (r < common) return OreType.COMMON;
        r -= common;
        if (r < rare) return OreType.RARE;
        r -= rare;
        if (r < epic) return OreType.EPIC;
        r -= epic;
        if (r < legendary) return OreType.LEGENDARY;
        return OreType.NONE;
    }
    
    /**
     * Ends a battle, removes all players, destroys game world
     */
    public final void destroy() {
        Set<CorePlayer> _players = new HashSet<>(players);
        for (CorePlayer cp : _players) {
            removePlayer(cp);
        }
        arena.decrementMatches();
        battleMode.removeBattle(this);
        gameWorld.destroy();
        ongoing = false;
        finished = true;
    }

    /**
     * Called when a battler leaves boundaries
     *
     * @param cp CorePlayer
     */
    protected abstract void failBattler(CorePlayer cp);
    
    /**
     * Called when a battler enters a goal area
     *
     * @param cp CorePlayer
     */
    protected abstract void winBattler(CorePlayer cp);
    
    /**
     * Called when a player surrenders (/ff, /leave)
     *
     * @param cp CorePlayer
     */
    public abstract void surrender(CorePlayer cp);
    
    protected void addBattleRequest(BattleRequest battleRequest) {
        battleRequests.put(battleRequest.getRequestName(), battleRequest);
    }
    
    public Set<String> getAvailableRequests(CorePlayer cp) {
        Set<String> availableRequests = new HashSet<>();
        if (battlers.containsKey(cp)) {
            availableRequests.addAll(battleRequests.keySet());
        } else {
            battleRequests.values().forEach(request -> {
                if (!request.isBattlerRequest()) {
                    availableRequests.add(request.getRequestName());
                }
            });
        }
        return availableRequests;
    }
    
    /**
     * @param cp Requesting Core Player
     * @param requestType BattleRequest Type Name
     * @param requestValue Requested Value
     */
    public void onRequest(CorePlayer cp, String requestType, @Nullable String requestValue) {
        if (battleRequests.containsKey(requestType)) {
            BattleRequest battleRequest = battleRequests.get(requestType);
            if (!battleRequest.isBattlerRequest() || battlers.containsKey(cp)) {
                if (battleRequest.isRequesting(cp)) {
                    battleRequest.removeRequester(cp);
                    return;
                }
                if (battleRequest.isOngoing()) {
                    if (requestValue == null) {
                        battleRequest.addRequester(cp,
                                battleRequest.isBattlerRequest() ? battlers.size() : players.size());
                    } else {
                        Core.getInstance().sendMessage(cp, "Someone is already requesting this!");
                    }
                } else {
                    battleRequest.startRequest(cp,
                            battleRequest.isBattlerRequest() ? battlers.size() : players.size(),
                            requestValue);
                }
            } else {
                Core.getInstance().sendMessage(cp, "You don't work here!");
            }
        } else {
            Core.getInstance().sendMessage(cp, "Can't request that here!");
        }
    }

    public void resetRequests() {
        for (BattleRequest br : battleRequests.values()) {
            br.clear();
        }
    }
    
    /**
     * Called when a Reset request passes
     */
    public abstract void reset();
    
    /**
     * Called when a Play To request passes
     *
     * @param playTo Play To Value
     */
    public abstract void setPlayTo(int playTo);
    
    /**
     * Called when a Pause request passes
     *
     * @param pauseTime Pause Time (Seconds)
     */
    public void pause(int pauseTime) {
        countdown = pauseTime;
        frozen = true;
    }
    
    /**
     * Reset all battlers
     */
    protected final void resetBattlers() {
        remainingPlayers.addAll(battlers.values());
        for (BP bp : battlers.values()) {
            spawnBattler(bp);
        }
    }

    /**
     * Add a spectator to the battle, if there is no
     * spectator spawn the spectator is set to GameMode 3
     * and forced to first person spectator of target
     *
     * @param cp CorePlayer
     * @param target Target
     */
    public void addSpectator(CorePlayer cp, CorePlayer target) {
        if (addPlayer(cp, BattleState.SPECTATOR)) {
            spectators.add(cp);
            cp.savePregameState();
            if (arena.getSpectatorSpawn() != null) {
                cp.getPlayer().teleport(arena.getSpectatorSpawn());
            } else {
                gameWorld.setSpectator(cp, target);
            }
        }
    }
    
    /**
     * Adds a global spectator to the battle when they enter
     * the global spectator boundaries of the arena, which is
     * a spectator who isn't teleport away at the end of the battle
     *
     * @param cp Core Player
     */
    public void addGlobalSpectator(CorePlayer cp) {
        if (addPlayer(cp, BattleState.SPECTATOR_GLOBAL)) {
            spectators.add(cp);
        }
    }

    /**
     * Remove spectator from the battle
     *
     * @param cp CorePlayer
     */
    public void removeSpectator(CorePlayer cp) {
        if (!spectators.contains(cp)) return;
        if (removePlayer(cp)) {
            spectators.remove(cp);
            if (cp.getPlayer().getGameMode().equals(org.bukkit.GameMode.SPECTATOR)) {
                cp.getPlayer().setSpectatorTarget(null);
            }
        }
    }
    
    public void removeGlobalSpectator(CorePlayer cp) {
        if (!spectators.contains(cp)) return;
        if (removePlayer(cp)) {
            spectators.remove(cp);
            cp.checkGlobalSpectate();
        }
    }

    /**
     * Called after a battler leaves the battle (/leave, /ff)
     *
     * @param cp Battler CorePlayer
     */
    protected abstract void leaveBattler(CorePlayer cp);

    /**
     * Called when a player wants to leave (/leave)
     *
     * @param cp CorePlayer
     */
    public final void leavePlayer(CorePlayer cp) {
        if (players.contains(cp)) {
            switch (cp.getBattleState()) {
                case BATTLER:
                    leaveBattler(cp);
                    break;
                case SPECTATOR:
                    removeSpectator(cp);
                    break;
                case REFEREE:
                    
                    break;
                case SPECTATOR_GLOBAL:
                    removeGlobalSpectator(cp);
                    break;
                default: break;
            }
        }
    }

    /**
     * Returns the current runtime of a battle as a formatted string
     *
     * @return Formatted Runtime String
     */
    protected String getRuntimeString() {
        long sec2 = (System.currentTimeMillis() - startedTime) / 100;
        long sec = sec2 / 10;
        String str = "";

        // Hours
        str += String.format("%02d", sec / 3600) + ":";
        // Minutes
        str += String.format("%02d", sec / 60 % 60) + ":";
        // Seconds
        str += String.format("%02d", sec % 60) + ".";
        str += String.format("%01d", sec2 % 10) + "";

        return str;
    }

    /**
     * Returns the current runtime of a battle as a formatted string
     *
     * @return Formatted Runtime String
     */
    protected String getRuntimeStringNoMillis() {
        long sec = (System.currentTimeMillis() - startedTime) / 1000;
        String str = "";

        // Hours
        str += String.format("%02d", sec / 3600) + ":";
        // Minutes
        str += String.format("%02d", sec / 60 % 60) + ":";
        // Seconds
        str += String.format("%02d", sec % 60);

        return str;
    }

    /**
     * Called every 0.1 second or on score updates
     * Updates the player scoreboards
     */
    public abstract void updateScoreboard();

    /**
     * Called every 1/20 second
     * Updates the field on occasion for events such as
     * auto-regenerating maps
     */
    public abstract void updateField();

    /**
     * Updates the experience bar of players in the game
     */
    public abstract void updateExperience();

    /**
     * Called when the game begins, removes glass boxes and allows
     * the world to be broken by specified tools and specified blocks
     */
    public void releaseBattlers() {
        BattleUtils.clearDome(gameWorld, spawns);
        gameWorld.setEditable(true);
        frozen = false;
        for (BattlePlayer bp : battlers.values()) {
            bp.getCorePlayer().refreshHotbar();
        }
    }
    
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * Returns the time the current round has been going on for
     *
     * @return Round Time
     */
    public double getRoundTime() {
        if (roundStartTime == 0) return 0;
        return (System.currentTimeMillis() - roundStartTime) / 1000.0;
    }

    public void checkWaiting() {
        Iterator<UUID> it = waitingPlayers.iterator();
        while (it.hasNext()) {
            CorePlayer cp = Core.getInstance().getPlayers().get(it.next());
            if (cp != null && cp.getOnlineState() == DBPlayer.OnlineState.HERE) {
                addBattler(cp);
                it.remove();
            }
        }
        if (waitingPlayers.isEmpty()) {
            waiting = false;
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), this::startBattle, 60L);
        }
    }

    /**
     * Called every second
     *
     * Counts down the start of a round
     */
    public void doCountdown() {
        if (!waiting && ongoing) {
            if (countdown >= 0) {
                if (countdown == 0) {
                    sendPlayerTitle(ChatColor.GREEN + "Go!", "", 5, 10, 5);
                    releaseBattlers();
                    roundStartTime = System.currentTimeMillis();
                } else /*if (countdown <= 3)*/ {
                    sendPlayerTitle(ChatColor.RED + "" + countdown + "...", "", 5, 10, 5);
                }
                countdown--;
            }
        }
    }

    /**
     * @return Round Started
     */
    public boolean isRoundStarted() {
        return countdown < 0;
    }

    /**
     * Set countdown and prevents world from being broken
     */
    public void startCountdown() {
        countdown = COUNTDOWN;
        gameWorld.setEditable(false);
        for (BattlePlayer bp : battlers.values()) {
            bp.getCorePlayer().refreshHotbar();
        }
    }

    /**
     * @return Arena Mode
     */
    public BattleMode getMode() {
        return battleMode;
    }

    /**
     * @return Arena
     */
    public Arena getArena() {
        return arena;
    }

    /**
     * @return Players
     */
    public Set<CorePlayer> getPlayers() {
        return players;
    }

    public List<BP> getBattlers() {
        return sortedBattlers;
    }

    public BP getBattler(CorePlayer cp) {
        return battlers.get(cp);
    }

    /**
     * Send a title to all players in this battle
     *
     * @param title Title
     * @param subtitle Subtitle
     * @param fadeIn Ticks
     * @param stay Ticks
     * @param fadeOut Ticks
     */
    protected void sendPlayerTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (CorePlayer dbp : players) {
            Chat.sendTitle(dbp, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    /**
     * @return Containing plugin
     */
    public CorePlugin<?> getPlugin() {
        return plugin;
    }
    
}
