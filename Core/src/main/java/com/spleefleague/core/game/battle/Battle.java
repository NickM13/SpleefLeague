/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game.battle;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatGroup;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.request.BattleRequest;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.game.GameWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nullable;

/**
 * A Battle is an object that contains an arena that the game
 * is played in and a set of players (battlers, spectators)
 *
 * @author NickM13
 */
public abstract class Battle<A extends Arena, BP extends BattlePlayer> {

    protected CorePlugin<?> plugin;
    
    protected org.bukkit.GameMode gameMode = org.bukkit.GameMode.SURVIVAL;
    protected GameWorld gameWorld;
    
    protected ChatGroup chatGroup;
    
    // Arena to play battle on
    protected final A arena;

    private final Class<BP> battlePlayerClass;
    
    // Some values of Arena that can be modified without changing the arena
    protected List<Dimension> borders = new ArrayList<>();
    protected List<Dimension> spectatorBorders = new ArrayList<>();
    protected List<Dimension> globalSpectatorBorders = new ArrayList<>();
    protected List<Location> spawns = new ArrayList<>();

    // Collections for players
    protected final Set<CorePlayer> players = new HashSet<>();
    protected final Set<CorePlayer> spectators = new HashSet<>();
    protected final Map<CorePlayer, BP> battlers = new HashMap<>();
    protected final List<BP> sortedBattlers = new ArrayList<>();
    
    private final Map<String, BattleRequest> battleRequests = new HashMap<>();
    
    // Ongoing battle stats
    protected boolean ongoing;
    protected long startedTime;
    protected long roundStartTime = 0;
    protected int remainingPlayers = 0;
    
    // Round countdown
    protected static final int COUNTDOWN = 3;
    protected int countdown = 0;

    public Battle(CorePlugin<?> plugin, List<CorePlayer> players, A arena, Class<BP> battlePlayerClass) {
        this.plugin = plugin;
        this.arena = arena;
        this.battlePlayerClass = battlePlayerClass;
        this.borders.addAll(arena.getBorders());
        this.spectatorBorders.addAll(arena.getSpectatorBorders());
        this.globalSpectatorBorders.addAll(arena.getGlobalSpectatorBorders());
        spawns.addAll(arena.getSpawns());
        this.gameWorld = arena.createGameWorld();
        this.chatGroup = new ChatGroup(plugin.getChatPrefix());
        players.forEach(this::addBattler);
    }

    /**
     * Start a battle
     */
    public final void startBattle() {
        arena.incrementMatches();
        arena.getMode().addBattle(this);
        startedTime = System.currentTimeMillis();
        ongoing = true;
        setupBattleRequests();
        setupBaseSettings();
        setupScoreboard();
        setupBattlers();
        sendStartMessage();
        startRound();
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
     * Called in startBattle()<br>
     * Send a message on the start of a battle
     */
    protected abstract void sendStartMessage();

    /**
     * Start a round
     * <br>Resets the field and its players, also used in Reset Request
     */
    public void startRound() {
        if (!ongoing) return;
        gameWorld.clearProjectiles();
        fillField();
        BattleUtils.fillDome(gameWorld, Material.GLASS, spawns);
        startCountdown();
        resetBattlers();
        updateScoreboard();
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
        EntityPlayer entityPlayer = ((CraftPlayer) bp.getPlayer()).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        entityPlayer.c(tag);
        
        bp.respawn();
    }

    /**
     * Called when a battler joins mid-game (if available)
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
            List<CorePlayer> toBattlefy = new ArrayList<>();
            if (arena.getMode().getTeamStyle().equals(ArenaMode.TeamStyle.TEAM)) {
                toBattlefy.addAll(cp.getParty().getPlayers());
            } else {
                toBattlefy.add(cp);
            }
            for (CorePlayer cp2 : toBattlefy) {
                Constructor<BP> c = battlePlayerClass.getDeclaredConstructor(CorePlayer.class, Battle.class);
                c.setAccessible(true);
                BP bp = c.newInstance(cp2, this);
                bp.setSpawn(getSpawn(battlers.size()));
                addPlayer(cp2, BattleState.BATTLER);
                battlers.put(cp2, bp);
                sortedBattlers.add(bp);
                spawnBattler(bp);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Battle.java: Failed to create new instance of a battle player " + battlePlayerClass);
        }
    }

    /**
     * Save the battlers stats
     * Called when a battler is removed from the battle
     */
    protected abstract void saveBattlerStats(CorePlayer cp);

    /**
     * Removes a battler from the battle without any further checking
     *
     * @param cp Battler Core Player
     */
    public final void removeBattler(CorePlayer cp) {
        if (!battlers.containsKey(cp)) return;
        battlers.remove(cp);
        removePlayer(cp);
        saveBattlerStats(cp);
    }

    /**
     * @return Ongoing Battle State
     */
    public boolean isOngoing() { return ongoing; }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }

    /**
     * Check if a player is within the bounding boxes of the arena
     *
     * @param cp Core Player
     * @return In Battler Bounds
     */
    private boolean isInBorder(CorePlayer cp) {
        for (Dimension border : borders) {
            if (border.isContained(new Point(cp.getPlayer().getLocation()))) {
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
        if (cp.getBattleState() == BattleState.BATTLER) {
            BP bp = battlers.get(cp);
            if (!bp.isFallen()) {
                if (!isRoundStarted()) {
                    if (e.getTo() != null && e.getFrom().getY() != e.getTo().getY()) {
                        e.getPlayer().teleport(new Location(e.getFrom().getWorld(),
                                e.getTo().getX(),
                                e.getFrom().getY(),
                                e.getTo().getZ(),
                                e.getTo().getYaw(),
                                e.getTo().getPitch()));
                    }
                } else if (e.getPlayer().getLocation().getBlock().isLiquid() || !isInBorder(cp)) {
                    failBattler(cp);
                }
            } else {
                if (!isInSpectatorBorder(cp)) {
                    e.setTo(getClosestBattler(cp).getPlayer().getLocation());
                }
            }
        } else if (cp.getBattleState() == BattleState.SPECTATOR || cp.getBattleState() == BattleState.SPECTATOR_GLOBAL) {
            if (!cp.getPlayer().getGameMode().equals(org.bukkit.GameMode.CREATIVE) && arena.hasTpBackSpectators() && (isInBorder(cp)/* || !isInArea(p)*/)) {
                cp.getPlayer().teleport(arena.getSpectatorSpawn());
            }
            if (cp.getBattleState() == BattleState.SPECTATOR_GLOBAL && !isInGlobalSpectatorBorder(cp)) {
                removeSpectator(cp);
            }
        }
    }

    /**
     * Do something if player right clicks
     *
     * @param cp CorePlayer
     */
    public void onRightClick(CorePlayer cp) {

    }

    /**
     * @param id Spawn Index
     * @return Spawn Location
     */
    protected Location getSpawn(int id) {
        if (id < spawns.size())
            return spawns.get(id);
        return spawns.get(0);
    }

    /**
     * Moderator cancel for battles
     */
    public void cancel() {
        chatGroup.sendMessage("Your match was cancelled by a moderator");
        endBattle();
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
            players.add(cp);
            gameWorld.addPlayer(cp);
            chatGroup.addPlayer(cp);
            cp.joinBattle(this, battleState);
            cp.getPlayer().getInventory().setHeldItemSlot(0);
            cp.getPlayer().getInventory().clear();
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
            cp.leaveBattle(arena.getPostGameWarp());
            chatGroup.removePlayer(cp);
            gameWorld.removePlayer(cp);
            players.remove(cp);
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
    protected abstract void endBattle(BP winner);
    
    /**
     * Ends a battle, removes all players, destroys game world
     */
    public final void endBattle() {
        Set<CorePlayer> _players = new HashSet<>(players);
        for (CorePlayer cp : _players) {
            removePlayer(cp);
        }
        arena.decrementMatches();
        arena.getMode().removeBattle(this);
        gameWorld.destroy();
        ongoing = false;
    }

    /**
     * Called when a battler leaves boundaries
     *
     * @param cp CorePlayer
     */
    protected abstract void failBattler(CorePlayer cp);

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
    
    /**
     * Called when a player requests the game to end (/endgame)
     *
     * @param cp CorePlayer
     */
    public abstract void requestEndGame(CorePlayer cp);

    /**
     * Called when a player requests to pause the game with specified
     * time (/pause <seconds>)
     *
     * @param cp CorePlayer
     * @param timeout Seconds
     */
    public abstract void requestPause(CorePlayer cp, int timeout);

    /**
     * Called when a player requests to pause the game (/pause)
     *
     * @param cp CorePlayer
     */
    public abstract void requestPause(CorePlayer cp);

    /**
     * Called when a player requests to reset the field (/reset)
     *
     * @param cp CorePlayer
     */
    public abstract void requestReset(CorePlayer cp);

    /**
     * Called when a player requests to change the
     * PlayTo score (/playto)
     *
     * @param cp CorePlayer
     */
    public abstract void requestPlayTo(CorePlayer cp);

    /**
     * Called when a player requests to change the
     * PlayTo score with specified score (/playto <score>)
     *
     * @param cp CorePlayer
     */
    public abstract void requestPlayTo(CorePlayer cp, int playTo);

    /**
     * Reset all battlers
     */
    protected final void resetBattlers() {
        remainingPlayers = battlers.size();
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
     * Called every 1 second or on score updates
     * Updates the player scoreboards
     */
    public abstract void updateScoreboard();

    /**
     * Called every 1/10 second
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
    public void releasePlayers() {
        BattleUtils.clearDome(gameWorld, spawns);
        gameWorld.setEditable(true);
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

    /**
     * Called every second
     *
     * Counts down the start of a round
     */
    public void doCountdown() {
        if (countdown >= 0) {
            if (countdown == 0) {
                sendPlayerTitle(ChatColor.GREEN + "Go!", "", 5, 10, 5);
                releasePlayers();
                roundStartTime = System.currentTimeMillis();
            } else if (countdown <= 3) {
                sendPlayerTitle(ChatColor.RED + "" + countdown + "...", "", 5, 10, 5);
            }
            countdown--;
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
    protected void startCountdown() {
        countdown = COUNTDOWN;
        gameWorld.setEditable(false);
    }

    /**
     * @return Arena Mode
     */
    public ArenaMode getMode() {
        return arena.getMode();
    }

    /**
     * @return Arena
     */
    public A getArena() {
        return arena;
    }

    /**
     * @return Players
     */
    public Set<CorePlayer> getPlayers() {
        return players;
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
