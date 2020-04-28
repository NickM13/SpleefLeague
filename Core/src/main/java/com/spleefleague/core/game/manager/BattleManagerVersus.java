/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game.manager;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.party.Party;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.queue.PlayerQueue;
import java.util.ArrayList;
import java.util.List;

/**
 * BattleManagerMultiStatic is a BattleManager that manages battles of
 * static sized games.  If a player leaves, the battle ends.  Used for 1v1s
 * 
 * @author NickM13
 */
public class BattleManagerVersus extends BattleManager {
    
    protected PlayerQueue queue;

    public BattleManagerVersus(ArenaMode mode) {
        super(mode);
        
        queue = new PlayerQueue();
        queue.initialize(displayName, this, false);
    }
    
    private ArrayList<CorePlayer> gatherPlayers(int num, int teamSize) {
        ArrayList<CorePlayer> gatheredPlayers = new ArrayList<>();
        ArrayList<CorePlayer> cplayers = queue.getMatchedPlayers(num, teamSize);
        if (cplayers == null) return null;
        for (CorePlayer cp : cplayers) {
            gatheredPlayers.add(cp);
            if (gatheredPlayers.size() == num) return gatheredPlayers;
        }
        return null;
    }
    
    @Override
    public int queuePlayer(CorePlayer cp) {
        queue.queuePlayer(cp);
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
                queue.queuePlayer(cp, arena);
                return 0;
            }
        }
    }

    @Override
    public void checkQueue() {
        for (int size : this.mode.getRequiredTeamSizes()) {
            if (queue.getQueueSize() >= this.mode.getRequiredTeams()) {
                ArrayList<CorePlayer> players = gatherPlayers(this.mode.getRequiredTeams(), size);
                if (players != null) {
                    startMatch(players, queue.getLastArenaName());
                }
            }
        }
    }
    
    @Override
    public void startMatch(List<CorePlayer> players, String name) {
        Arena arena = Arena.getByName(name, mode);
        Battle<?, ?> battle;
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
                        .getDeclaredConstructor(List.class, mode.getArenaClass())
                        .newInstance(players, arena);
                battle.startBattle();
                battles.add(battle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
