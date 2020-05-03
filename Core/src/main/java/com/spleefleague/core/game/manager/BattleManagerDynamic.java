/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game.manager;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.party.Party;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.queue.PlayerQueue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * BattleManagerTeam is a BattleManager that manages battles of
 * any number of players, and once it has begun players can't
 * enter the battle but if a player leaves it does not stop
 * 
 * @author NickM13
 */
public class BattleManagerDynamic extends BattleManager {
    
    protected static Long DELAY_START_TIME = 1000L;
    
    protected PlayerQueue queue;
    
    protected Long delayedStart = null;
    
    public BattleManagerDynamic(BattleMode mode) {
        super(mode);
        
        queue = new PlayerQueue();
        queue.initialize(displayName, this, false);
    }
    
    private ArrayList<CorePlayer> gatherPlayers(int num) {
        return queue.getMatchedPlayers(num, 1);
    }
    
    @Override
    public int queuePlayer(CorePlayer cp) {
        Party party = cp.getParty();
        if (party == null) {
            queue.queuePlayer(cp);
        } else {
            for (CorePlayer cp2 : party.getPlayers()) {
                queue.queuePlayer(cp2);
            }
        }
        return 0;
    }
    
    @Override
    public int queuePlayer(CorePlayer cp, Arena arena) {
        if (arena == null) {
            return queuePlayer(cp);
        } else {
            if (!arena.isAvailable()) {
                Core.getInstance().sendMessage(cp, arena.getDisplayName() + " is currently disabled.");
                return 3;
            } else {
                Party party = cp.getParty();
                if (party == null) {
                    Core.getInstance().sendMessage(cp, "You must been in a party to join this queue!");
                    return 1;
                }
                if (arena.getTeamSize() != party.getPlayers().size()) {
                    Core.getInstance().sendMessage(cp, "That arena requires a team size of " + arena.getTeamSize() + "!");
                    return 2;
                }
                queue.queuePlayer(cp, arena);
                return 0;
            }
        }
    }
    
    @Override
    public void checkQueue() {
        if (delayedStart != null) {
            if (delayedStart < System.currentTimeMillis()) {
                if (queue.getQueueSize() < this.mode.getRequiredTeams()) {
                    // Some players left, not enough players message :(
                    delayedStart = null;
                } else {
                    if (queue.getQueueSize() >= this.mode.getRequiredTeams()) {
                        int size = Math.max(this.mode.getRequiredTeams(), queue.getQueueSize());
                        ArrayList<CorePlayer> players = gatherPlayers(size);
                        if (players != null) {
                            startMatch(players, queue.getLastArenaName());
                        }
                    }
                }
                delayedStart = null;
            } else {
                Core.getInstance().sendMessage("A " + this.mode.getDisplayName() + " game will be beginning soon! BattleManagerDynamic.java:100");
            }
        } else {
            if (queue.getQueueSize() >= this.mode.getRequiredTeams()) {
                Core.getInstance().sendMessage("A " + this.mode.getDisplayName() + " game will be beginning soon!");
                delayedStart = System.currentTimeMillis() + DELAY_START_TIME;
            }
        }
    }
    
    @Override
    public void startMatch(List<CorePlayer> players, String arenaName) {
        Arena arena = Arenas.get(arenaName, mode);
        if (arena == null) return;
        Battle<?> battle;
        for (CorePlayer cp : players) {
            Party party = cp.getParty();
            if (party != null) {
                party.leave(cp);
            }
            if (cp.isInBattle()) {
                Core.getInstance().unqueuePlayerGlobally(cp);
                return;
            }
        }
        try {
            if (arena.isAvailable()) {
                for (CorePlayer cp : players) {
                    Core.getInstance().unqueuePlayerGlobally(cp);
                }
                    battle = battleClass
                            .getDeclaredConstructor(List.class, Arena.class)
                            .newInstance(players, arena);
                battle.startBattle();
                battles.add(battle);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            CoreLogger.logError("A battle failed to begin on arena " + arena.getDisplayName() + "\n" + e.getMessage());
        }
    }

}
