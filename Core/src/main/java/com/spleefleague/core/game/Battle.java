/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatGroup;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.Dimension;
import com.spleefleague.core.util.Point;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.core.world.GameWorld;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author NickM13
 * @param <P>
 * @param <A>
 */
public class Battle<P extends DBPlayer, A extends Arena> {
    
    protected CorePlugin plugin;
    
    protected GameMode gameMode = GameMode.SURVIVAL;
    protected GameWorld gameWorld;
    
    protected String playersFormatted;
    
    protected ChatGroup chatGroup = null;
    
    // Arena to play battle on
    protected final A arena;
    
    // Some values of Arena that can be modified without changing the arena
    protected List<Dimension> borders = new ArrayList<>();
    protected List<Dimension> spectatorBorders = new ArrayList<>();
    protected List<Location> spawns = new ArrayList<>();
    
    // Set of all players, including spectators
    protected final Set<P> players = new HashSet<>();
    protected final Set<P> spectators = new HashSet<>();
    
    // Battle is still ongoing
    protected boolean ongoing;
    protected long startedTime;
    protected long roundStartTime = 0;
    
    // Round countdown
    protected static final int COUNTDOWN = 3;
    protected int countdown = 0;
    
    private static final String[] defeatedSynonyms = {"defeated", "clobbered", "smashed",
        "pulverized", "clanked", "whapped", "wam jam'd", "destroyed", "ended the career of",
        "wang jangled", "cracked", "stolen elo from", "cheated in their match against", "given a new one to",
        "bested"};
    
    protected String randomDefeatSynonym() {
        Random r = new Random();
        return defeatedSynonyms[r.nextInt(defeatedSynonyms.length)];
    }

    public Battle(CorePlugin plugin, List<DBPlayer> players, A arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.arena.incrementMatches();
        for (Dimension border : arena.getBorders()) {
            this.borders.add(border);
            this.spectatorBorders.add(border.expand(20));
        }
        for (Location spawn : arena.getSpawns()) {
            spawns.add(spawn);
        }
        this.gameWorld = arena.createGameWorld();
        this.chatGroup = new ChatGroup();
        for (DBPlayer dbp : players) {
            P p = (P) plugin.getPlayers().get(dbp);
            this.players.add(p);
        }
        setupPlayers();
    }
    
    public CorePlugin getPlugin() {
        return plugin;
    }
    
    public void addBattler(DBPlayer dbp) {
        P p = (P) plugin.getPlayers().get(dbp);
        players.add(p);
        gameWorld.addPlayer(dbp.getPlayer(), BattleState.BATTLER);
        chatGroup.addPlayer(dbp);
        p.savePregameState();
    }
    
    public void removeBattler(DBPlayer dbp) {
        P p = (P) plugin.getPlayers().get(dbp);
        players.remove(p);
        removePlayer(p);
    }
    
    protected void setupPlayers() {
        playersFormatted = "";
        List<String> playerNames = new ArrayList<>();
        Iterator<P> pit = players.iterator();
        while (pit.hasNext()) {
            DBPlayer dbp = pit.next();
            dbp.savePregameState();
            dbp.joinBattle(this, BattleState.BATTLER);
            this.chatGroup.addPlayer(dbp);
            this.gameWorld.addPlayer(dbp.getPlayer(), BattleState.BATTLER);
            playerNames.add(dbp.getDisplayName());
        }
        setupPlayerNameFormat(playerNames);
    }
    
    protected void setupPlayerNameFormat(List<String> playerNames) {
        for (int i = 0; i < playerNames.size(); i++) {
            if (i > 0 && i < players.size() - 1) {
                playersFormatted += Chat.DEFAULT + ", ";
            } else if (i == players.size() - 1) {
                playersFormatted += Chat.DEFAULT + " and ";
            }
            playersFormatted += Chat.PLAYER_NAME + playerNames.get(i);
        }
    }
    
    public boolean isOngoing() { return ongoing; }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }
    
    protected void sendStartMessage() { }
    
    protected void startBattle() {
        ongoing = true;
        startedTime = System.currentTimeMillis();
        sendStartMessage();
        
        startRound();
    }
    
    protected boolean isInBorder(P p) {
        for (Dimension border : borders) {
            if (border.isContained(new Point(p.getPlayer().getLocation()))) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isInSpectatorBorder(P p) {
        for (Dimension border : spectatorBorders) {
            if (border.isContained(new Point(p.getPlayer().getLocation()))) {
                return true;
            }
        }
        return false;
    }
    
    public P getClosestBattler(P p) {
        return players.iterator().next();
    }
    
    public boolean isFallen(P p) {
        return false;
    }
    
    public void onMove(P p, PlayerMoveEvent e) {
        if (p.getBattleState() == BattleState.BATTLER) {
            if (!p.getBattle().isFallen(p)) {
                if (!isRoundStarted()) {
                    if (e.getFrom().getY() != e.getTo().getY()) {
                        e.getPlayer().teleport(new Location(e.getFrom().getWorld(),
                                e.getTo().getX(),
                                e.getFrom().getY(),
                                e.getTo().getZ(),
                                e.getTo().getYaw(),
                                e.getTo().getPitch()));
                    }
                } else if (e.getPlayer().getLocation().getBlock().isLiquid() || !isInBorder(p)) {
                    failPlayer(p);
                }
            } else {
                if (!isInSpectatorBorder(p)) {
                    e.setTo(getClosestBattler(p).getPlayer().getLocation());
                }
            }
        } else if (p.getBattleState() == BattleState.SPECTATOR) {
            if (!p.getPlayer().getGameMode().equals(GameMode.CREATIVE) && arena.hasTpBackSpectators() && (isInBorder(p)/* || !isInArea(p)*/)) {
                p.getPlayer().teleport(arena.getSpectatorSpawn());
            }
        }
    }
    
    public void onBlockBreak(P dbp) {
        
    }
    
    public void onSneakStart(P dbp) {
        
    }
    public void onSneaking(P dbp) {
        
    }
    public void onSneakEnd(P dbp) {
        
    }
    
    public void onSlotChange(P dbp, int newSlot) {
        
    }
    
    public void onRightClick(P dbp) {
        
    }
    
    public void checkProjectile(ProjectileHitEvent e) {
        gameWorld.checkProjectile(e);
    }
    
    protected void fillField() {
        
    }
    
    protected Location getSpawn(int id) {
        if (id < spawns.size())
            return spawns.get(id);
        return spawns.get(0);
    }
    
    protected void fillHotbar(P p) {
        p.getPlayer().getInventory().setHeldItemSlot(0);
        p.getPlayer().getInventory().clear();
    }
    
    protected void startRound() {
        if (!ongoing) return;
        gameWorld.clearProjectiles();
        fillField();
        genGlassBoxes();
        startCountdown();
        resetPlayers();
    }
    
    public void cancel() {
        chatGroup.sendMessage("Your match was cancelled by a moderator");
        endBattle();
    }
    
    protected void removePlayer(P p) {
        p.loadPregameState(arena.getPostGameWarp());
        p.leaveBattle();
        chatGroup.removePlayer(p);
        gameWorld.removePlayer(p.getPlayer());
    }
    
    public void endBattle() {
        for (P p : players) {
            removePlayer(p);
        }
        arena.decrementMatches();
        gameWorld.destroy();
        ongoing = false;
    }
    
    protected void failPlayer(P p) {
        updateScoreboard();
    }
    
    public boolean surrender(P p) {
        return false;
    }
    
    public void requestEndGame(P p) { }
    
    public void requestPause(P p, int timeout) { }
    public void requestPause(P p) { }
    
    public void requestReset(P p) { }
    
    public void requestPlayTo(P p) { }
    public void requestPlayTo(P p, int playTo) { }
    
    protected void resetPlayer(P p) { }
    protected void resetPlayers() { }
    
    public void addSpectator(P dbp, P target) {
        if (!players.contains(dbp)) {
            dbp.joinBattle(this, BattleState.SPECTATOR);
            spectators.add(dbp);
            players.add(dbp);
            chatGroup.addPlayer(dbp);
            dbp.savePregameState();
            gameWorld.addPlayer(dbp.getPlayer(), BattleState.SPECTATOR);
            if (arena.getSpectatorSpawn() != null) {
                dbp.getPlayer().teleport(arena.getSpectatorSpawn());
            } else {
                gameWorld.setSpectator(dbp.getPlayer(), target.getPlayer());
            }
        }
    }
    
    public void fixSpectators(P target) {
        gameWorld.fixSpectators(target.getPlayer());
    }
    
    public void removeSpectator(P p) {
        if (players.contains(p)) {
            p.leaveBattle();
            spectators.remove(p);
            players.remove(p);
            if (p.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
                p.getPlayer().setSpectatorTarget(null);
            removePlayer(p);
        }
    }
    
    public void leavePlayer(P dbp) {
        if (players.contains(dbp)) {
            switch (dbp.getBattleState()) {
                case BATTLER:
                    this.surrender(dbp);
                    break;
                case SPECTATOR:
                    this.removeSpectator(dbp);
                    break;
                case REFEREE:
                    
                    break;
                default: break;
            }
        }
    }
    
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
    
    private void genGlassBoxes() {
        for (Location loc : spawns) {
            BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            gameWorld.setBlock(pos.add(new BlockPosition(-1, 0,  0)), Material.GLASS.createBlockData());
            gameWorld.setBlock(pos.add(new BlockPosition( 1, 0,  0)), Material.GLASS.createBlockData());
            gameWorld.setBlock(pos.add(new BlockPosition( 0, 0, -1)), Material.GLASS.createBlockData());
            gameWorld.setBlock(pos.add(new BlockPosition( 0, 0,  1)), Material.GLASS.createBlockData());
            gameWorld.setBlock(pos.add(new BlockPosition(-1, 1,  0)), Material.GLASS.createBlockData());
            gameWorld.setBlock(pos.add(new BlockPosition( 1, 1,  0)), Material.GLASS.createBlockData());
            gameWorld.setBlock(pos.add(new BlockPosition( 0, 1, -1)), Material.GLASS.createBlockData());
            gameWorld.setBlock(pos.add(new BlockPosition( 0, 1,  1)), Material.GLASS.createBlockData());
            gameWorld.setBlock(pos.add(new BlockPosition( 0, 2,  0)), Material.GLASS.createBlockData());
        }
    }
    private void removeGlassBoxes() {
        for (Location loc : spawns) {
            BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            gameWorld.breakBlock(pos.add(new BlockPosition(-1, 0,  0)));
            gameWorld.breakBlock(pos.add(new BlockPosition( 1, 0,  0)));
            gameWorld.breakBlock(pos.add(new BlockPosition( 0, 0, -1)));
            gameWorld.breakBlock(pos.add(new BlockPosition( 0, 0,  1)));
            gameWorld.breakBlock(pos.add(new BlockPosition(-1, 1,  0)));
            gameWorld.breakBlock(pos.add(new BlockPosition( 1, 1,  0)));
            gameWorld.breakBlock(pos.add(new BlockPosition( 0, 1, -1)));
            gameWorld.breakBlock(pos.add(new BlockPosition( 0, 1,  1)));
            gameWorld.breakBlock(pos.add(new BlockPosition( 0, 2,  0)));
        }
    }
    
    public void updateScoreboard() {
        
    }
    
    public void updateField() {
        
    }
    
    public void updateExperience() {
        //chatGroup.setExperience((float)(getRoundTime() % 1), (int)(getRoundTime()));
    }
    
    public void releasePlayers() {
        removeGlassBoxes();
        gameWorld.setEdittable(true);
    }
    
    public double getRoundTime() {
        if (roundStartTime == 0) return 0;
        return (System.currentTimeMillis() - roundStartTime) / 1000.0;
    }
    
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
    
    public boolean isRoundStarted() {
        return countdown < 0;
    }
    
    protected void startCountdown() {
        countdown = COUNTDOWN;
        gameWorld.setEdittable(false);
    }
    
    public ArenaMode getMode() {
        return arena.getMode();
    }
    
    public A getArena() {
        return arena;
    }
    
    public Set<P> getPlayers() {
        return players;
    }
    
    protected void sendPlayerMessage(String msg) {
        for (P p : players) {
            Chat.sendMessageToPlayer(p, msg);
        }
    }
    
    protected void sendPlayerTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (P p : players) {
            Chat.sendTitle(p, title, subtitle, fadeIn, stay, fadeOut);
        }
    }
    
}
