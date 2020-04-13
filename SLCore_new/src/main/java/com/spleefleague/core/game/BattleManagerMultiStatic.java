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
 * BattleManagerMultiStatic is a BattleManager that manages battles of
 * static sized games.  If a player leaves, the battle ends.  Used for 1v1s
 * 
 * @author NickM13
 * @param <B>
 */
public class BattleManagerMultiStatic<B extends Battle> extends BattleManager<B> {
    
    protected PlayerQueue queue;

    public BattleManagerMultiStatic(ArenaMode mode) {
        super(mode);
        
        queue = new PlayerQueue();
        queue.initialize(displayName, this, false);
    }
    
    private ArrayList<DBPlayer> gatherPlayers(int num, int teamSize) {
        ArrayList<DBPlayer> splayers = new ArrayList<>();
        ArrayList<DBPlayer> dbplayers = queue.getMatchedPlayers(num, teamSize);
        if (dbplayers == null) return null;
        for (DBPlayer dbp : dbplayers) {
            splayers.add(Core.getInstance().getPlayers().get(dbp.getPlayer()));
            if (splayers.size() == num) return splayers;
        }
        return null;
    }
    
    @Override
    public int queuePlayer(DBPlayer dbp) {
        queue.queuePlayer(dbp);
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
                queue.queuePlayer(dbp, arena);
                return 0;
            }
        }
    }

    @Override
    public void checkQueue() {
        for (int size : this.mode.getRequiredTeamSizes()) {
            if (queue.getQueueSize() >= this.mode.getRequiredTeams()) {
                ArrayList<DBPlayer> players = gatherPlayers(this.mode.getRequiredTeams(), size);
                if (players != null) {
                    startMatch(players, queue.getLastArenaName());
                }
            }
        }
    }
    
    @Override
    public void startMatch(List<DBPlayer> players, String name) {
        Arena arena = Arena.getByName(name, mode);
        B sb = null;
        for (DBPlayer dbp : players) {
            CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
            Party party = cp.getParty();
            if (party != null) {
                party.leave(cp);
            }
            if (CorePlugin.isInBattleGlobal(dbp.getPlayer())) {
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
            e.printStackTrace();
        }
    }

}
