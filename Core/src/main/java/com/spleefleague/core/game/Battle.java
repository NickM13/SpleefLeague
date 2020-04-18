/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatGroup;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.game.GameWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * A Battle is an object that contains an arena that the game
 * is played in and a set of players (battlers, spectators)
 *
 * @author NickM13
 * @param <A>
 */
public abstract class Battle<A extends Arena> {

    protected CorePlugin<?> plugin;
    
    protected org.bukkit.GameMode gameMode = org.bukkit.GameMode.SURVIVAL;
    protected GameWorld gameWorld;
    
    protected ChatGroup chatGroup;
    
    // Arena to play battle on
    protected final A arena;

    private final Class<? extends BattlePlayer> battlePlayerClass;
    
    // Some values of Arena that can be modified without changing the arena
    protected List<Dimension> borders = new ArrayList<>();
    protected List<Dimension> spectatorBorders = new ArrayList<>();
    protected List<Location> spawns = new ArrayList<>();

    // Collections for players
    protected final Set<CorePlayer> players = new HashSet<>();
    protected final Set<CorePlayer> spectators = new HashSet<>();
    protected final Map<CorePlayer, BattlePlayer> battlers = new HashMap<>();
    protected final List<BattlePlayer> sortedBattlers = new ArrayList<>();
    
    // Ongoing battle stats
    protected boolean ongoing;
    protected long startedTime;
    protected long roundStartTime = 0;
    protected int remainingPlayers = 0;
    
    // Round countdown
    protected static final int COUNTDOWN = 3;
    protected int countdown = 0;

    public Battle(CorePlugin<?> plugin, List<CorePlayer> players, A arena, Class<? extends BattlePlayer> battlePlayerClass) {
        this.plugin = plugin;
        this.arena = arena;
        this.battlePlayerClass = battlePlayerClass;
        this.arena.incrementMatches();
        for (Dimension border : arena.getBorders()) {
            this.borders.add(border);
            this.spectatorBorders.add(border.expand(20));
        }
        spawns.addAll(arena.getSpawns());
        this.gameWorld = arena.createGameWorld();
        this.chatGroup = new ChatGroup();
        players.forEach(this::addBattler);
    }

    /**
     * Start a battle (round 0)
     */
    public final void startBattle() {
        setupBaseSettings();
        setupBattlers();
        sendStartMessage();
        startRound();
        ongoing = true;
        startedTime = System.currentTimeMillis();
    }

    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    protected abstract void setupBaseSettings();

    /**
     * Initialize the players, called in startBattle()
     */
    protected abstract void setupBattlers();

    /**
     * Send a message on the start of a battle
     */
    protected abstract void sendStartMessage();

    /**
     * Start a round
     * Generally would reset field and players
     */
    protected void startRound() {
        if (!ongoing) return;
        gameWorld.clearProjectiles();
        fillField();
        BattleUtils.fillDome(gameWorld, Material.GLASS, spawns);
        startCountdown();
        resetBattlers();
        updateScoreboard();
    }

    protected abstract void fillField();

    /**
     * Called when a battler is spawned into a battle or when
     * they should be respawned
     *
     * @param bp BattlePlayer
     */
    protected final void spawnBattler(BattlePlayer bp) {
        bp.getCorePlayer().setGameMode(gameMode);
        bp.getPlayer().setWalkSpeed(0.2f);
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
                Constructor<? extends BattlePlayer> c = battlePlayerClass.getDeclaredConstructor(CorePlayer.class, Battle.class);
                c.setAccessible(true);
                BattlePlayer bp = c.newInstance(cp2, this);
                addPlayer(cp2, BattleState.BATTLER);
                battlers.put(cp2, bp);
                sortedBattlers.add(bp);
                spawnBattler(bp);
                setupBattleInventory(cp2);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the battlers stats
     * Called when a battler is removed from the battle
     */
    protected abstract void saveBattlerStats(CorePlayer cp);

    /**
     * Removes a battler from the battle
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
     * @return In Spectator Bounds
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
     * Returns the closest battler (non-fallen) to the player
     * If no players are alive, returns null
     *
     * @param cp CorePlayer
     * @return Closest Alive Player (null if none)
     */
    protected final CorePlayer getClosestBattler(CorePlayer cp) {
        CorePlayer closest = null;
        double closeDist = 0, dist;
        for (BattlePlayer bp2 : battlers.values()) {
            if (!cp.equals(bp2.getCorePlayer()) && !bp2.isFallen()) {
                dist = cp.getPlayer().getLocation().distance(bp2.getPlayer().getLocation());
                if (closest == null || dist < closeDist) {
                    closest = bp2.getCorePlayer();
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
            BattlePlayer bp = battlers.get(cp);
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
        } else if (cp.getBattleState() == BattleState.SPECTATOR) {
            if (!cp.getPlayer().getGameMode().equals(org.bukkit.GameMode.CREATIVE) && arena.hasTpBackSpectators() && (isInBorder(cp)/* || !isInArea(p)*/)) {
                cp.getPlayer().teleport(arena.getSpectatorSpawn());
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
     * Clears the player's inventory for battle related items
     *
     * @param cp CorePlayer
     */
    protected abstract void setupBattleInventory(CorePlayer cp);

    /**
     * Moderator cancel for battles
     */
    public void cancel() {
        chatGroup.sendMessage("Your match was cancelled by a moderator");
        endBattle();
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
     * Ends a battle, removes all players, destroys game world
     */
    public final void endBattle() {
        Set<CorePlayer> _players = new HashSet<>(players);
        for (CorePlayer cp : _players) {
            removePlayer(cp);
        }
        arena.decrementMatches();
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
    public void surrender(CorePlayer cp) { }

    /**
     * Called when a player requests the game to end (/endgame)
     *
     * @param cp CorePlayer
     */
    public void requestEndGame(CorePlayer cp) { }

    /**
     * Called when a player requests to pause the game with specified
     * time (/pause <seconds>)
     *
     * @param cp CorePlayer
     * @param timeout Seconds
     */
    public void requestPause(CorePlayer cp, int timeout) { }

    /**
     * Called when a player requests to pause the game (/pause)
     *
     * @param cp CorePlayer
     */
    public void requestPause(CorePlayer cp) { }

    /**
     * Called when a player requests to reset the field (/reset)
     *
     * @param cp CorePlayer
     */
    public void requestReset(CorePlayer cp) { }

    /**
     * Called when a player requests to change the
     * PlayTo score (/playto)
     *
     * @param cp CorePlayer
     */
    public void requestPlayTo(CorePlayer cp) { }

    /**
     * Called when a player requests to change the
     * PlayTo score with specified score (/playto <score>)
     *
     * @param cp CorePlayer
     */
    public void requestPlayTo(CorePlayer cp, int playTo) { }

    /**
     * Resets player
     *
     * @param cp CorePlayer
     */
    protected abstract void resetPlayer(CorePlayer cp);

    /**
     * Reset all battlers
     */
    protected final void resetBattlers() {
        remainingPlayers = battlers.size();
        for (CorePlayer cp : battlers.keySet()) {
            resetPlayer(cp);
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
     * When the battler teleports there is a bug with
     * spigot that causes players to not follow and remain
     * glitched in place, this fixes it
     *
     * @param target Player to fix spectators of
     */
    public void fixSpectators(CorePlayer target) {
        gameWorld.fixSpectators(target);
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
            if (cp.getPlayer().getGameMode().equals(org.bukkit.GameMode.SPECTATOR))
                cp.getPlayer().setSpectatorTarget(null);
        }
    }

    /**
     * Called when a battler wants to leave (/leave, /ff)
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
                    removeBattler(cp);
                    leaveBattler(cp);
                    break;
                case SPECTATOR:
                    removeSpectator(cp);
                    break;
                case REFEREE:

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
