/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.party.Party;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.queue.PlayerQueue;
import com.spleefleague.core.util.database.DBPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * BattleManagerTeam is a BattleManager that manages battles of 
 * 
 * @author NickM13
 * @param <B>
 */
public class BattleManagerMultiDynamic<B extends Battle> extends BattleManager<B> {
    
    protected static Long DELAY_TIME = 1 * 1000L;
    
    protected PlayerQueue queue;
    
    protected Long delayedStart = null;
    
    public BattleManagerMultiDynamic(ArenaMode mode) {
        super(mode);
        
        queue = new PlayerQueue();
        queue.initialize(displayName, this, false);
    }
    
    private ArrayList<DBPlayer> gatherPlayers(int num) {
        ArrayList<DBPlayer> splayers = new ArrayList<>();
        ArrayList<DBPlayer> dbplayers = queue.getMatchedPlayers(num, 1);
        if (dbplayers == null) return null;
        for (DBPlayer dbp : dbplayers) {
            splayers.add(Core.getInstance().getPlayers().get(dbp.getPlayer()));
            if (splayers.size() == num) return splayers;
        }
        return null;
    }
    
    @Override
    public int queuePlayer(DBPlayer dbp) {
        CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
        Party party = cp.getParty();
        if (party == null) {
            queue.queuePlayer(dbp);
        } else {
            for (CorePlayer cp2 : party.getPlayers()) {
                queue.queuePlayer(cp2);
            }
        }
        return 0;
    }
    
    @Override
    public int queuePlayer(DBPlayer dbp, Arena arena) {
        if (arena == null) {
            return queuePlayer(dbp);
        } else {
            if (arena.isPaused()) {
                Core.sendMessageToPlayer(dbp, arena.getDisplayName() + " is currently disabled.");
                return 3;
            } else {
                CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
                Party party = cp.getParty();
                if (party == null) {
                    Core.sendMessageToPlayer(dbp, "You must been in a party to join this queue!");
                    return 1;
                }
                if (arena.getTeamSize() != party.getPlayers().size()) {
                    Core.sendMessageToPlayer(dbp, "That arena requires a team size of " + arena.getTeamSize() + "!");
                    return 2;
                }
                queue.queuePlayer(dbp, arena);
                return 0;
            }
        }
    }

    @Override
    public void checkQueue() {
        if (delayedStart != null) {
            if (delayedStart < System.currentTimeMillis()) {
                if (queue.getQueueSize() < this.mode.getRequiredTeams()) {
                    // Not enough players message :(
                    delayedStart = null;
                } else {
                    if (queue.getQueueSize() >= this.mode.getRequiredTeams()) {
                        int size = Math.max(this.mode.getRequiredTeams(), queue.getQueueSize());
                        ArrayList<DBPlayer> players = gatherPlayers(size);
                        if (players != null) {
                            startMatch(players, queue.getLastArenaName());
                        }
                    }
                }
                delayedStart = null;
            } else {
                // About to begin message!
            }
        } else {
            if (queue.getQueueSize() >= this.mode.getRequiredTeams()) {
                Core.getInstance().sendMessage("A " + this.mode.getDisplayName() + " game will be beginning soon!");
                delayedStart = System.currentTimeMillis() + DELAY_TIME;
            }
        }
    }
    
    @Override
    public void startMatch(List<DBPlayer> players, String name) {
        Arena arena = Arena.getByName(name, mode);
        if (arena == null) return;
        B sb = null;
        int size = -1;
        for (DBPlayer dbp : players) {
            CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
            Party party = cp.getParty();
            if (party != null) {
                party.leave(cp);
            }
            if (CorePlugin.isInBattleGlobal(cp.getPlayer())) {
                Core.getInstance().unqueuePlayerGlobally(cp);
                return;
            }
        }
        try {
            if (arena.isAvailable()) {
                for (DBPlayer dbp : players) {
                    CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
                    Core.getInstance().unqueuePlayerGlobally(cp);
                }
                sb = (B) battleClass
                        .getDeclaredConstructor(List.class, mode.arenaClass)
                        .newInstance(players, arena);
                sb.startBattle();
                battles.add(sb);
            }
        } catch (Exception e) {
            System.out.println("A battle failed to begin on arena " + arena.getDisplayName());
            e.printStackTrace();
        }
    }

}
